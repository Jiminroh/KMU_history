# train.py
import argparse

import os
import torch
import torchvision
import torch.optim as optim
from torch.utils.data import DataLoader
from utils import Compose, Resize, ToTensor, collate_fn, Nomal
from dataset import TACO_Dataset
from model import mask_rcnn, mask_rcnn_2

from tqdm import tqdm
import wandb

parser = argparse.ArgumentParser(description="mask rcnn",
                                 formatter_class=argparse.ArgumentDefaultsHelpFormatter)

# parser of hyperparameter
parser.add_argument("--batch_size", default=16, type=int, dest="batch_size")
parser.add_argument("--lr", default=1e-3, type=float, dest="lr")
parser.add_argument("--max_size", default=200, type=int, dest="max_size")
parser.add_argument("--num_workers", default=2, type=int, dest="num_workers")
parser.add_argument("--num_epoch", default=10, type=int, dest="num_epoch")
parser.add_argument("--base_path", type=str, dest="base_path")

# save model
parser.add_argument("--save_path", type=str, dest="save_path")

# wandb
parser.add_argument("--wandb_project_name", type=str, dest="wandb_project_name", default="DL_project") 
parser.add_argument("--wandb_entity_name", type=str, dest="wandb_entity_name", default="jimin_") 
parser.add_argument("--wandb_name", type=str, dest="wandb_name", default="test_1") 

args = parser.parse_args()

# initailize parser
BATCH_SIZE = args.batch_size
LEARNING_RATE = args.lr
MAX_SIZE = args.max_size
NUM_WORKERS = args.num_workers
NUM_EPOCH = args.num_epoch
BASE_PATH = args.base_path
SAVE_PATH = args.save_path

WANDB_PROJECT = args.wandb_project_name 
WANDB_ENTITY = args.wandb_entity_name 
WANDB_NAME = args.wandb_name 

config = {
    "EPOCH" : NUM_EPOCH,
    "BATCH_SIZE" : BATCH_SIZE,
    "LEARNING_RATE" : LEARNING_RATE,
    "SAVE_PATH" : SAVE_PATH
}

def main():
    # wandb
    run = wandb.init(project=WANDB_PROJECT, entity=WANDB_ENTITY, name=WANDB_NAME, reinit=True)

    # --------------- hyperparameter ---------------
    batch_size = BATCH_SIZE
    lr = LEARNING_RATE
    max_size = MAX_SIZE
    num_workers = NUM_WORKERS
    num_epochs = NUM_EPOCH
    base_path = BASE_PATH #'drive/MyDrive/KMU/4학년 1학기/딥러닝/팀프로젝트 7조/plz_base'
    save_path = os.path.join(BASE_PATH, 'save_model', SAVE_PATH) # model.pth

    device = 'cuda:0' if torch.cuda.is_available() else 'cpu'

    # --------------- transforms ---------------
    transforms_train = Compose([
        Resize((max_size, max_size)),
        Nomal(),
        ToTensor()])

    # --------------- Define dataset & dataloader ---------------
    train_dataset = TACO_Dataset(base_path, 'train', 0, transforms=transforms_train)
    val_dataset = TACO_Dataset(base_path, 'val', 0, transforms=transforms_train)

    train_loader = DataLoader(
        train_dataset, batch_size=batch_size, shuffle=True, 
        num_workers=num_workers, collate_fn=collate_fn)
    val_loader = DataLoader(
        val_dataset, batch_size=batch_size, shuffle=False, 
        num_workers=num_workers, collate_fn=collate_fn)

     # --------------- create model ---------------
    model = mask_rcnn()
    # backbone network(resnet+fpn) requires_grad = False
    for name, param in model.named_parameters():
        if name.split('.')[0] in ['backbone']:
            pass
        else:
            param.requires_grad = False
    model.to(device)

    # --------------- Optimizer ---------------
    params = [p for p in model.parameters() if p.requires_grad]
    optimizer = optim.Adam(
        params, lr=lr, weight_decay=1e-5)

    for epoch in range(num_epochs):
        # calculate loss
        total = 0
        cnt = 0

        # train
        train_loop = tqdm(train_loader)
        print('\n-----train start-----')
        model.train()
        for i, (images, targets) in enumerate(train_loop):
            print(f'epoch :{epoch}')

            optimizer.zero_grad()
            images = [image.to(device) for image in images]
            targets = [{k: v.to(device) for k, v in t.items()} for t in targets]

            losses = model(images, targets)
            loss = sum(loss for loss in losses.values())
            
            total += loss.item()
            cnt += 1

            if i%10==0:
                # wandb log for step
                wandb.log({'cls_loss': losses['loss_classifier'].item()})
                wandb.log({'mask_loss': losses['loss_mask'].item()})
                wandb.log({'bbox_reg_loss': losses['loss_box_reg'].item()})
                wandb.log({'objness_loss': losses['loss_objectness'].item()})
                wandb.log({'batch_loss': loss.item()})

            # backward
            loss.backward()
            optimizer.step()
            
            # update loop 
            train_loop.set_postfix(loss=loss.item())
        
        # wandb log for epoch
        wandb.log({'total_loss':total/cnt})

        
        # validation
        print('\n-----validation start-----')
        model.eval()
        with torch.no_grad():
            model.train() # validation loss를 측정하기위해 변경, 그러나 gradient를 update하지 않음
            for i, (images, targets) in enumerate(val_loader):
                optimizer.zero_grad()
                images = [image.to(device) for image in images]
                targets = [{k: v.to(device) for k, v in t.items()} for t in targets]

                losses = model(images, targets)
                loss = sum(loss for loss in losses.values())
                
                if i%10==0:
                    print(
                        f"step: {i}, cls_loss: {losses['loss_classifier'].item():.5f}, "\
                        f"mask_loss: {losses['loss_mask'].item():.5f}, "\
                        f"bbox_reg_loss: {losses['loss_box_reg'].item():.5f}, "\
                        f"nobj_loss: {losses['loss_objectness'].item():.5f}, "\
                        f"total_loss: {loss.item():.5f}")
        
    # save model
    torch.save(model, save_path)

if __name__ == '__main__':
    print('3')
    main()
        
                
                