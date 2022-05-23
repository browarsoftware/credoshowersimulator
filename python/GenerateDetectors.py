import numpy as np
import random as rand
gridSizeX = 1600
gridSizeY = 1600

transictionX = gridSizeX / 2
transictionY = gridSizeY / 2

detectorSizeXMin = 10
detectorSizeXMax = 10
detectorSizeYMin = 10
detectorSizeYMax = 10

eps = 0.000001
number = 25
class Point:
    def __init__(self, xcoord=0, ycoord=0):
        self.x = xcoord
        self.y = ycoord

class Rectangle:
    def __init__(self, id, bottom_left, top_right, value):
        self.id = id
        self.bottom_left = bottom_left
        self.top_right = top_right
        self.value = value

    def intersects(self, other):
        return not (self.top_right.x - eps < other.bottom_left.x + eps
                    or self.bottom_left.x + eps > other.top_right.x - eps
                    or self.top_right.y - eps < other.bottom_left.y + eps
                    or self.bottom_left.y + eps > other.top_right.y - eps)

count = 0
allRects = []
while count < number:
    sizeX = rand.randrange(detectorSizeXMin, detectorSizeXMax + 1)
    sizeY = rand.randrange(detectorSizeYMin, detectorSizeYMax + 1)

    positionX = rand.randrange(0, gridSizeX - sizeX + 1)
    positionY = rand.randrange(0, gridSizeY - sizeY + 1)

    r1 = Rectangle(count, Point(positionX, positionY), Point(positionX + sizeX, positionY + sizeY), 45)
    over = False
    if len(allRects) > 0:
        a = 0
        while a < len(allRects) and not(over):
            r2 =  allRects[a]
            if r1.intersects(r2):
                over = True
            a = a + 1
    if not(over):
        allRects.append(r1)
        count = count + 1

with open('detectors_random.conf', 'w') as the_file:
    the_file.write('id,x,y,w,h\n')
    for a in range(len(allRects)):
        rect = allRects[a]
        x = (rect.bottom_left.x + rect.top_right.x) / 2  - transictionX
        #x = int(x)
        y = (rect.bottom_left.y + rect.top_right.y) / 2 - transictionY
        #y = int(x)
        w = abs(rect.bottom_left.x - rect.top_right.x)
        h = abs(rect.bottom_left.y - rect.top_right.y)

        x = x / 100
        y = y / 100
        w = w / 100
        h = h / 100
        the_file.write(str(rect.id) + ',' + str(x) + ',' +  str(y) + ',' + str(w) + ',' + str(h) + '\n')


