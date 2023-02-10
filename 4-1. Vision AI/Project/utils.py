import numpy as np
import copy
from PIL import Image

import torch
import torchvision
import torchvision.transforms.functional as TF
from torchvision.ops import nms

import wandb


class Compose:
    def __init__(self, transforms):
        self.transforms = transforms

    def __call__(self, image, target):
        for transform in self.transforms:
            image, target = transform(
                image, target)

        return image, target


class Resize:
    def __init__(self, size, interpolation=Image.BILINEAR):
        self.size = size
        self.interpolation = interpolation

    def __call__(self, image, target):
        w, h = image.size
        image = image.resize(self.size)
        
        target['boxes'][:, [0, 2]] *= self.size[0] / w
        target['boxes'][:, [1, 3]] *= self.size[1] / h
        
        return image, target

class Normal:
    def __call__(self, image, target):
        normalize = torchvision.transforms.Normalize(mean=[0.4854, 0.4562, 0.4065], std=[0.2293, 0.2244, 0.2250])

        image = normalize(image)
        return image, target



class ToTensor:
    def __call__(self, image, target):
        image = TF.to_tensor(image)
        
        return image, target

def collate_fn(batch):
    return tuple(zip(*batch))

def replace_dataset_classes(dataset, class_map):
    """ Replaces classes of dataset based on a dictionary"""
    class_new_names = list(set(class_map.values()))
    class_new_names.sort()
    class_originals = copy.deepcopy(dataset['categories'])
    dataset['categories'] = []
    class_ids_map = {}  # map from old id to new id

    # Assign background id 0
    has_background = False
    if 'Background' in class_new_names:
        if class_new_names.index('Background') != 0:
            class_new_names.remove('Background')
            class_new_names.insert(0, 'Background')
        has_background = True

    # Replace categories
    for id_new, class_new_name in enumerate(class_new_names):

        # Make sure id:0 is reserved for background
        id_rectified = id_new
        if not has_background:
            id_rectified += 1

        category = {
            'supercategory': '',
            'id': id_rectified,  # Background has id=0
            'name': class_new_name,
        }
        dataset['categories'].append(category)
        # Map class names
        for class_original in class_originals:
            if class_map[class_original['name']] == class_new_name:
                class_ids_map[class_original['id']] = id_rectified

    # Update annotations category id tag
    for ann in dataset['annotations']:
        ann['category_id'] = class_ids_map[ann['category_id']]

def calculate_loss(losses):
    loss = sum(loss for loss in losses.values())
    return loss

def decode_output(output):
    'convert tensors to numpy arrays'
    bbs = output['boxes'].cpu().detach().numpy().astype(np.uint16) # output의 bounding box
    labels = np.array([i for i in output['labels'].cpu().detach().numpy()])
    confs = output['scores'].cpu().detach().numpy() # output bounding box 의 confidence score

    ixs = nms(torch.tensor(bbs.astype(np.float32)), torch.tensor(confs), 0.3) # nms 수행

    bbs, confs, labels = [tensor[ixs] for tensor in [bbs, confs, labels]]

    if len(ixs) == 1:
        bbs, confs, labels= [np.array([tensor]) for tensor in [bbs, confs, labels]]
    return bbs.tolist(), confs.tolist(), labels.tolist()

def make_wandb_visual(image, bbs, confs, labels):
    wandb_results = {}
    '''
    class_labels = {
            0 : 'background',
            1 : '작업자(작업복착용)',
            2 : '작업자(작업복미착용)', 
            3 : '화물트럭',
            4 : '지게차',
            5 : '헨드파레트카',
            6 : '롤테이너',
            7 : '운반수레',
            8 : '흡연',
            9 : '보관렉',
            10 : '적제물류',
            11 : '문류',
            12 : 'SO-04',
            13 : 'SO-05',
            14 : '도크',
            15 : '출입문',
            16 : '화물승강기',
            17 : '차단멀티탭',
            18 : '멀티탭',
            19 : '개인전열기구',
            20 : '소화기',
            21 : '작업안전구역',
            22 : '용접작업구역',
            23 : '지게차이동영역',
            24 : '출입제한구역',
            25 : '화재대피로',
            26 : '안전팬스',
            27 : '화기',
            28 : 'SO-20',
            29 : '이물질',
            30 : '가연물',
            31 : '샌드위치판넬',
        }
    '''
    class_labels = {
        0 : 'workers',
        1 : 'head',
        2 : 'helmet',
        3 : 'person'
    }

    img_np = torch.permute(image.to('cpu'), (1, 2, 0)).numpy().astype(np.float64)

    # for bbox
    bbs = [[a/400 for a in single] for single in bbs]
    wandb_results['predictions'] = {
        'box_data': [
                {
                'position': {
                    "minX": bb[0],
                    "maxX": bb[1],
                    "minY": bb[2],
                    "maxY": bb[3]
                },
                'class_id' : label,
                'box_caption': class_labels[label],
                'scores' : {
                    'conf': conf,
                    }
                } for bb, conf, label in zip(bbs, confs, labels)],
        'class_labes': class_labels
        } 
    

    img = wandb.Image(img_np, boxes = wandb_results)

    wandb.log({"Inference": img})

