import os
import torch
import numpy as np
from PIL import Image
from pycocotools.coco import COCO

from torch.utils.data import Dataset

class Warehouse_Dataset(Dataset):
    def __init__(self, path, mode, transforms=None):

        # transforms 
        self.transforms = transforms
        # file path
        self.path = os.path.join(path, 'warehouse_data', mode)
        self.ann_path = os.path.join(self.path, f'warehouse_coco_{mode}.json')
        # coco function
        self.coco = COCO(self.ann_path)
        dataset = self.del_uc(self.coco)
        self.coco.dataset = dataset
        self.coco.createIndex()
        # image num
        self.image_ids = list(self.coco.imgToAnns.keys())
        print(f'{mode}_dataset length : {len(self.image_ids)}')

    def __len__(self):
        return len(self.image_ids)

    def __getitem__(self, idx):
        image_id = self.image_ids[idx]
        file_name = self.coco.loadImgs(image_id)[0]['file_name']
        file_name = f'{self.path}/{file_name}.jpg'
        image = Image.open(file_name).convert('RGB')

        annot_ids = self.coco.getAnnIds(imgIds=image_id)
        annots = [x for x in self.coco.loadAnns(annot_ids) if x['image_id'] == image_id]
        
        boxes = np.array([annot['bbox'] for annot in annots], dtype=np.float32)
        boxes[:, 2] = boxes[:, 0] + boxes[:, 2]
        boxes[:, 3] = boxes[:, 1] + boxes[:, 3]

        labels = np.array([self.label2num(annot['category_id']) for annot in annots], dtype=np.int64)

        target = {
            'boxes': boxes,
            'labels': labels
            }
        
        if self.transforms is not None:
            image, target = self.transforms(image, target)
            
        target['boxes'] = torch.as_tensor(target['boxes'], dtype=torch.float32)
        target['labels'] = torch.as_tensor(target['labels'])

        return image, target
    def label2num(self, label):
        category = {
            'WO-01' : 1,
            'WO-02' : 2,
            'WO-03' : 3,
            'WO-04' : 4,
            'WO-05' : 5,
            'WO-06' : 6,
            'WO-07' : 7,
            'WO-08' : 8,
            'SO-01' : 9,
            'SO-02' : 10,
            'SO-03' : 11,
            'SO-04' : 12,
            'SO-05' : 13,
            'SO-06' : 14,
            'SO-07' : 15,
            'SO-08' : 16,
            'SO-09' : 17,
            'SO-10' : 18,
            'SO-11' : 19,
            'SO-12' : 20,
            'SO-13' : 21,
            'SO-14' : 22,
            'SO-15' : 23,
            'SO-16' : 24,
            'SO-17' : 25,
            'SO-18' : 26,
            'SO-19' : 27,
            'SO-20' : 28,
            'SO-21' : 29,
            'SO-22' : 30,
            'SO-23' : 31,
        }
        return category[label]

    def del_uc(self, coco):
        dataset = coco.dataset
        for idx, ann in enumerate(dataset['annotations']):
            if ann['category_id'] == "UC-06":
                del dataset['annotations'][idx]
        return dataset


    
if __name__ == '__main__':
    import torch.utils
    from torch.utils.data import DataLoader
    from utils import collate_fn
    import torchvision
    from utils import *

    path = '.'
    mode = 'train'

    transforms = Compose([
        Resize((400, 400)),
        ToTensor()
    ])

    dataset = Warehouse_Dataset(path, mode, transforms=None)
    
    print(dataset[1])
    
    
    

    
    