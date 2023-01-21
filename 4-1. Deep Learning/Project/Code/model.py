import torch
import torchvision
from torchvision.models.detection.faster_rcnn import FastRCNNPredictor
from torchvision.models.detection.mask_rcnn import MaskRCNNPredictor

def mask_rcnn():
    num_classes = 9 # 0 -> background 
    hidden_layer = 64

    # --------------- create model ---------------
    model = torchvision.models.detection.maskrcnn_resnet50_fpn(weight=torchvision.models.detection.MaskRCNN_ResNet50_FPN_Weights.DEFAULT)

    # roi cls head 
    in_features = model.roi_heads.box_predictor.cls_score.in_features
    model.roi_heads.box_predictor = FastRCNNPredictor(in_features, num_classes)

    # roi mask head
    in_features_mask = model.roi_heads.mask_predictor.conv5_mask.in_channels
    model.roi_heads.mask_predictor = MaskRCNNPredictor(in_features_mask, hidden_layer, num_classes)
    
    return model

def mask_rcnn_2():
    import torch
    import torchvision
    from torchvision.models.detection import MaskRCNN
    from torchvision.models.detection.anchor_utils import AnchorGenerator
    
    # load a pre-trained model for classification and return
    # only the features
    backbone = torchvision.models.mobilenet_v2(weights=torchvision.models.MobileNet_V2_Weights.DEFAULT).features
    # MaskRCNN needs to know the number of
    # output channels in a backbone. For mobilenet_v2, it's 1280
    # so we need to add it here
    backbone.out_channels = 1280
    
    # let's make the RPN generate 5 x 3 anchors per spatial
    # location, with 5 different sizes and 3 different aspect
    # ratios. We have a Tuple[Tuple[int]] because each feature
    # map could potentially have different sizes and
    # aspect ratios
    anchor_generator = AnchorGenerator(sizes=((32, 64, 128, 256, 512),),
                                        aspect_ratios=((0.5, 1.0, 2.0),))
    
    # let's define what are the feature maps that we will
    # use to perform the region of interest cropping, as well as
    # the size of the crop after rescaling.
    # if your backbone returns a Tensor, featmap_names is expected to
    # be ['0']. More generally, the backbone should return an
    # OrderedDict[Tensor], and in featmap_names you can choose which
    # feature maps to use.
    roi_pooler = torchvision.ops.MultiScaleRoIAlign(featmap_names=['0'],
                                                    output_size=7,
                                                    sampling_ratio=2)
    
    mask_roi_pooler = torchvision.ops.MultiScaleRoIAlign(featmap_names=['0'],
                                                         output_size=14,
                                                         sampling_ratio=2)
    # put the pieces together inside a MaskRCNN model
    model = MaskRCNN(
        backbone,
        num_classes=9,
        rpn_anchor_generator=anchor_generator,
        box_roi_pool=roi_pooler,
        mask_roi_pool=mask_roi_pooler
    )
    return model

if __name__ == '__main__':
    from torchvision.ops import nms
    import numpy as np
    # def decode_output(output):
    #     'convert tensors to numpy arrays'
    #     bbs = output['boxes'].cpu().detach().numpy().astype(np.uint16) # output의 bounding box
    #     labels = np.array([i for i in output['labels'].cpu().detach().numpy()])
    #     confs = output['scores'].cpu().detach().numpy() # output bounding box 의 confidence score
    #     masks = output['masks'].cpu().detach().numpy()

    #     ixs = nms(torch.tensor(bbs.astype(np.float32)), torch.tensor(confs), 0.05) # nms 수행

    #     bbs, confs, labels, masks = [tensor[ixs] for tensor in [bbs, confs, labels, masks]]

    #     if len(ixs) == 1:
    #         bbs, confs, labels, masks = [np.array([tensor]) for tensor in [bbs, confs, labels, masks]]
    #     return bbs.tolist(), confs.tolist(), labels.tolist(), masks.tolist()

    # model = mask_rcnn()
    # model.eval()
    # x = [torch.rand(3, 300, 400)]
    # outputs = model(x)
    
    # for ix, output in enumerate(outputs):
    #     bbs, confs, labels, masks = decode_output(output)
    
    model = mask_rcnn_2()
        
    for name, param in model.named_parameters():
        if name.split('.')[0] in ['backbone']:
            pass
        else:
            param.requires_grad = False
            print(name)
   