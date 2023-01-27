import torch
import torchvision
import torchvision.models as models
from torchvision.models.detection import FCOS
from torchvision.models.detection.anchor_utils import AnchorGenerator

def fcos(num_classes):
    backbone = models.mobilenet_v2(weights=models.MobileNet_V2_Weights.DEFAULT).features
    backbone.out_channels = 1280

    anchor_generator = AnchorGenerator(
        sizes=((8,), (16,), (32,), (64,), (128,)),
        aspect_ratios=((1.0,),)
    )

    model = FCOS(
        backbone,
        num_classes=num_classes,
        anchor_generator=anchor_generator,
    )
    return model

if __name__ == '__main__':
    torch.manual_seed(0)
    
    model = fcos(3)

    model.eval()
    x = [torch.rand(3, 300, 400)]
    predictions = model(x)
    
    boxes, scores, labels = predictions[0].values()
    print(len(boxes))
    print(len(scores))
    print(len(labels))