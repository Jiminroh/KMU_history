# train.py
import argparse

import os
import torch
from torch.utils.data import DataLoader
from utils import Compose, Resize, ToTensor, Normal, collate_fn, decode_output, make_wandb_visual
from warehouse_dataset import Warehouse_Dataset
from dataset import Helmet_Dataset

from torchmetrics.detection.mean_ap import MeanAveragePrecision
from pprint import pprint

import wandb
from tqdm import tqdm


parser = argparse.ArgumentParser(description="fcos",
                                 formatter_class=argparse.ArgumentDefaultsHelpFormatter)

# parser of hyperparameter
parser.add_argument("--batch_size", default=16, type=int, dest="batch_size")
parser.add_argument("--max_size", default=200, type=int, dest="max_size")
parser.add_argument("--num_workers", default=2, type=int, dest="num_workers")
parser.add_argument("--num_classes", default=2, type=int, dest="num_classes")
parser.add_argument("--base_path", type=str, dest="base_path")

# load model
parser.add_argument("--data", type=str, default='warehouse', dest="data")
parser.add_argument("--load_path", type=str, dest="load_path")

# wandb
parser.add_argument("--wandb_project_name", type=str, dest="wandb_project_name", default="CV_project") 
parser.add_argument("--wandb_entity_name", type=str, dest="wandb_entity_name", default="jimin_") 
parser.add_argument("--wandb_name", type=str, dest="wandb_name", default="test") 

args = parser.parse_args()

# initailize parser
BATCH_SIZE = args.batch_size
MAX_SIZE = args.max_size
NUM_WORKERS = args.num_workers
DATA = args.data
BASE_PATH = args.base_path
LOAD_PATH = args.load_path

WANDB_PROJECT = args.wandb_project_name 
WANDB_ENTITY = args.wandb_entity_name 
WANDB_NAME = args.wandb_name 


def main():
    # wandb
    run = wandb.init(project=WANDB_PROJECT, entity=WANDB_ENTITY, name=WANDB_NAME, reinit=True)

    # --------------- hyperparameter ---------------
    batch_size = BATCH_SIZE
    max_size = MAX_SIZE
    num_workers = NUM_WORKERS
    data = DATA
    base_path = BASE_PATH #'drive/MyDrive/KMU/4학년 1학기/시인지/프로젝트
    load_path = os.path.join(BASE_PATH, 'save_model', 'helmet', LOAD_PATH)

    device = 'cuda:0' if torch.cuda.is_available() else 'cpu'

    # --------------- transforms ---------------
    transforms = Compose([
        Resize((max_size, max_size)),
        ToTensor(),
        Normal()
    ])

    # --------------- Define dataset & dataloader ---------------
    test_dataset = Helmet_Dataset(base_path, 'test', transforms=transforms)

    test_loader = DataLoader(
        test_dataset, batch_size=batch_size, shuffle=True, 
        num_workers=num_workers, collate_fn=collate_fn)

    # --------------- load model ---------------
    model = torch.load(load_path)

    # metric
    metric = MeanAveragePrecision()

    with torch.no_grad():
        # test
        print('-----test start-----')
        loop = tqdm(test_loader)
        model.eval()
        for i, (images, targets) in enumerate(loop):
            images = [image.to(device) for image in images]
            targets = [{k: v.to(device) for k, v in t.items()} for t in targets]

            outputs = model(images)

            metric.update(outputs, targets)
            
            for idx, output in enumerate(outputs):
                image = images[idx]
                bbs, confs, labels = decode_output(output)
                
                make_wandb_visual(image, bbs, confs, labels)
        
    pprint(metric.compute())
            


if __name__ == '__main__':
    print('2')
    main()
    
        
                
                