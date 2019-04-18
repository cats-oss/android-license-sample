# Parallax scrolling effect

This sample is the license page in [WinTicket app](https://www.winticket.jp/).

![gif-license][gif-license]

## Stories of each scene

### SCENE1
* Pull the camera angle
* The road lines move away and move backwards
* The bicycle does not move because the camera only pulls

### SCENE2
* Pedal the bicycle
* Move the road line
* The towers approach (intervals are equal)
* The trees, the churches and the houses which are randomly generated approach
* The sun raises

### SCENE3
* Basic movement is the same as second scene
* The length of the second scene and the third scene is variable
* The sun changes to the moon

### SCENE4
* It grows dark and it is night
* The camera is fixed
* Only the bicycle goes and disappears

### SCENE5
* Tilt the camera from bottom to top
* Widen the road
* Make the mountain parallax

### SCENE6
* Scale in the logo
* Show the credit

### ALL SCENE

* Make the mountains appear closer

## Objects

### Design drawing

![img-design-drawing][img-design-drawing]

### Bicycle

The bicycle is switched by the following three images.

|Left|Center|Right|
|:-:|:-:|:-:|
|![img-bike-left][img-bike-left]|![img-bike-center][img-bike-center]|![img-bike-right][img-bike-right]|

### Lines on road

This lines move along this function.    

<img src="https://latex.codecogs.com/gif.latex?y&space;=&space;\frac{x^n}{h^n}" />

* Graph

e.g. h(height)=2000, n=6.5

![img-graph][img-graph]

* Spread sheet

You can change values and see how it works.  

https://docs.google.com/spreadsheets/d/18L7ELAOvYFlnyRuj-T_5yDBZDgzLFjkvj8rHRkcF230/edit#gid=0

### Sun and moon

<img src="https://latex.codecogs.com/gif.latex?y&space;=&space;r\sin\theta" />  
<img src="https://latex.codecogs.com/gif.latex?x&space;=&space;r\cos\theta" />  

### Towers, churches, homes

Change a pivot.

* Left objects: lower right  
`(pivotX, pivotY) = (view.width, view.height)`

* Right objects: lower left  
`(pivotX, pivotY) = (0, view.height)`

## License

```
Copyright 2019 CyberAgent, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[img-bike-center]: /arts/bike_center.png
[img-bike-left]: /arts/bike_left.png
[img-bike-right]: /arts/bike_right.png
[img-design-drawing]: /arts/design-drawing.jpg
[img-graph]: /arts/graph.jpg
[gif-license]: /arts/license.gif


