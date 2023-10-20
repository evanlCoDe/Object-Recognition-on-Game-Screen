# Object-Recognition-on-Game-Screen
## Abstract
Because I really enjoy shooting games. I want to use current AI technology to identify objects and characters that appear in the game in videos.

## Methods
- Use Java to build an application that can capture screen images.
- Find a mature object recognition model and deploy it in the application.
- Extract the ROI (Region of Interest) of objects on the game screen and draw the ROI region on the screen.
## Model and Library
- JavaCV：https://github.com/bytedeco/javacv
  - JavaCV uses wrappers from the JavaCPP Presets of commonly used libraries by researchers in the field of computer vision.
- Yolo v4：https://github.com/AlexeyAB/darknet
 - Object recognition model
