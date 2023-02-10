import os
import numpy as np
from PIL import Image
from pycocotools.coco import COCO
import csv
import json
import copy

import torch
from torch.utils.data import Dataset

class TACO_Dataset(Dataset):
    def __init__(self, path, mode, round, transforms=None):
        # file path
        self.data_path = os.path.join(path, 'data')
        self.ann_path = os.path.join(self.data_path, 'annotations')

        if round != None:
            self.ann_path += "_" + str(round) + "_" + mode + ".json"
        else:
            self.ann_path += ".json"

        # coco function
        self.coco = COCO(self.ann_path)

        # image num
        self.image_ids = list(self.coco.imgToAnns.keys())

        # transforms
        self.transforms = transforms

        # load class map
        class_map = {}
        with open(os.path.join(path, "taco_config", "map_new.csv")) as csvfile:
            reader = csv.reader(csvfile)
            class_map = {row[0]:row[1] for row in reader}

        # Load dataset
        dataset = json.load(open(self.ann_path, 'r'))

        # Replace dataset original classes before calling the coco Constructor
        # Some classes may be assigned background to remove them from the dataset
        self.replace_dataset_classes(dataset, class_map)

        self.coco.dataset = dataset
        class_ids = self.coco.getCatIds()
        print(f'class number : {len(class_ids)}')
        self.coco.createIndex()

        print('\nCategories')
        for cat in self.coco.dataset['categories']:
            val = list(cat.values())
            print(f'{val[1]}: {val[2]}')
        
        print(f'\nDataset length : {len(self.image_ids)}')

    def __len__(self):
        return len(self.image_ids)

    def __getitem__(self, idx):
        image_id = self.image_ids[idx]
        file_name = self.coco.loadImgs(image_id)[0]['file_name']
        file_name = f'{self.data_path}/{file_name}'
        image = Image.open(file_name).convert('RGB')

        annot_ids = self.coco.getAnnIds(imgIds=image_id)
        annots = [x for x in self.coco.loadAnns(annot_ids) if x['image_id'] == image_id]
        
        boxes = np.array([annot['bbox'] for annot in annots], dtype=np.float32)
        boxes[:, 2] = boxes[:, 0] + boxes[:, 2]
        boxes[:, 3] = boxes[:, 1] + boxes[:, 3]

        labels = np.array([annot['category_id'] for annot in annots], dtype=np.int32)
        masks = np.array([self.coco.annToMask(annot) for annot in annots], dtype=np.uint8)

        area = np.array([annot['area'] for annot in annots], dtype=np.float32)
        iscrowd = np.array([annot['iscrowd'] for annot in annots], dtype=np.uint8)

        target = {
            'boxes': boxes,
            'masks': masks,
            'labels': labels,
            'area': area,
            'iscrowd': iscrowd}
        
        if self.transforms is not None:
            image, target = self.transforms(image, target)
            
        target['boxes'] = torch.as_tensor(target['boxes'], dtype=torch.float32)
        target['masks'] = torch.as_tensor(target['masks'], dtype=torch.uint8)
        target['labels'] = torch.as_tensor(target['labels'], dtype=torch.int64)
        target['area'] = torch.as_tensor(target['area'], dtype=torch.float32)
        target['iscrowd'] = torch.as_tensor(target['iscrowd'], dtype=torch.uint8)            

        return image, target

    def replace_dataset_classes(self, dataset, class_map):
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

# if __name__ == '__main__':
#     import torch.utils

#     path = '.'
#     dataset = TACO_Dataset(path, 'train', 3)

#     # image= dataset[0][0]
#     # boxes, mask, labels, area, iscrowd = dataset[0][1].values()
#     # print(image)
#     # print(mask)

#     print(len(dataset))

    
    
    

    
    