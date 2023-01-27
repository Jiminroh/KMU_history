import os
import torch
import numpy as np
from PIL import Image
from pycocotools.coco import COCO

from torch.utils.data import Dataset

class Helmet_Dataset(Dataset):
    def __init__(self, path, mode, transforms=None):

        # transforms 
        self.transforms = transforms
        # file path
        self.path = os.path.join(path, 'data', mode)
        self.ann_path = os.path.join(self.path, '_annotations.coco.json')
        # coco function
        self.coco = COCO(self.ann_path)
        # image num
        self.image_ids = list(self.coco.imgToAnns.keys())
        print(f'{mode}_dataset length : {len(self.image_ids)}')

    def __len__(self):
        return len(self.image_ids)

    def __getitem__(self, idx):
        image_id = self.image_ids[idx]
        file_name = self.coco.loadImgs(image_id)[0]['file_name']
        file_name = f'{self.path}/{file_name}'

        image = Image.open(file_name)

        annot_ids = self.coco.getAnnIds(imgIds=image_id)
        annots = [x for x in self.coco.loadAnns(annot_ids) if x['image_id'] == image_id]
        
        boxes = np.array([annot['bbox'] for annot in annots], dtype=np.float32)
        boxes[:, 2] = boxes[:, 0] + boxes[:, 2]
        boxes[:, 3] = boxes[:, 1] + boxes[:, 3]

        labels = np.array([annot['category_id'] for annot in annots], dtype=np.int32)

        area = np.array([annot['area'] for annot in annots], dtype=np.float32)
        iscrowd = np.array([annot['iscrowd'] for annot in annots], dtype=np.uint8)

        target = {
            'boxes': boxes,
            'labels': labels,
            'area': area,
            'iscrowd': iscrowd}
        
        if self.transforms is not None:
            image, target = self.transforms(image, target)
            
        target['boxes'] = torch.as_tensor(target['boxes'], dtype=torch.float32)
        target['labels'] = torch.as_tensor(target['labels'], dtype=torch.int64)
        target['area'] = torch.as_tensor(target['area'], dtype=torch.float32)
        target['iscrowd'] = torch.as_tensor(target['iscrowd'], dtype=torch.uint8)            

        return image, target

    
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

    dataset = Helmet_Dataset(path, mode, transforms=transforms)
    dataloader = DataLoader(
        dataset=dataset,
        batch_size=2,
        shuffle=False,
        collate_fn=collate_fn
    )
    from tqdm import tqdm
    loop = tqdm(dataloader)
    # device = 'cuda' if torch.cuda.is_available() else 'cpu'
    for i, (images, targets) in enumerate(loop):
        print(images[0].shape)
        print(targets)
        break
    # data = dataset[1][0].shape
    # image = Image.open(file_name).convert('RGB')
    # print(data)
    
    

    
    