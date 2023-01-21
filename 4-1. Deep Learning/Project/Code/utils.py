import numpy as np
import copy
from PIL import Image

import torch
import torchvision.transforms.functional as TF
from torchvision.ops import nms

import wandb


class Compose:
    def __init__(self, transforms):
        self.transforms = transforms

    def __call__(self, image, target):
        for transform in self.transforms:
            image, target = transform(image, target)

        return image, target


class Resize:
    def __init__(self, size, interpolation=Image.BILINEAR):
        self.size = size
        self.interpolation = interpolation

    def __call__(self, image, target):
        w, h = image.size
        image = image.resize(self.size)

        _masks = target['masks'].copy()
        masks = np.zeros((_masks.shape[0], self.size[0], self.size[1]))
        
        for i, v in enumerate(_masks):
            v = Image.fromarray(v).resize(self.size, resample=Image.BILINEAR)
            masks[i] = np.array(v, dtype=np.uint8)

        target['masks'] = masks
        target['boxes'][:, [0, 2]] *= self.size[0] / w
        target['boxes'][:, [1, 3]] *= self.size[1] / h
        
        return image, target
        
class Nomal:
    def __call__(self, image, target):
        image = TF.Normalize((0.485, 0.456, 0.406), (0.229, 0.224, 0.225))

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
    masks = output['masks'].cpu().detach().numpy()

    ixs = nms(torch.tensor(bbs.astype(np.float32)), torch.tensor(confs), 0.3) # nms 수행

    bbs, confs, labels, masks = [tensor[ixs] for tensor in [bbs, confs, labels, masks]]

    if len(ixs) == 1:
        bbs, confs, labels, masks = [np.array([tensor]) for tensor in [bbs, confs, labels, masks]]
    return bbs.tolist(), confs.tolist(), labels.tolist(), masks


def make_wandb_visual(image, t_mask, bbs, confs, labels, masks):
    wandb_results = {}

    class_labels = {
        0: 'background',
        1: 'Can',
        2: 'Foam',
        3: 'Glass',
        4: 'Metal',
        5: 'Other',
        6: 'Paper',
        7: 'Plastic',
        8: 'Vinyl' 
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
                } for bb, conf, label, mask in zip(bbs, confs, labels, masks)],
        'class_labes': class_labels
        } 
    
    # for mask
    masks = [mask.transpose((1,2,0)) for mask in masks]
    for idx in range(len(masks)):
        masks[idx][masks[idx] >= 0.3] = 1
        masks[idx][masks[idx] < 0.3] = 0

    for idx, label in enumerate(labels):
        masks[idx][masks[idx] == 1] = label
    
    sum_mask = np.zeros_like(masks[0].squeeze())
    
    for mask in masks:
        sum_mask += mask.squeeze()
    
    sum_mask = sum_mask.squeeze().astype(np.float64)
    print('t',t_mask.shape)

    img = wandb.Image(img_np,
     boxes = wandb_results,
     masks={
        'predictions': {
            'mask_data': sum_mask,
            'class_labels': class_labels
        }
    })

    wandb.log({"Inference": img})
  


