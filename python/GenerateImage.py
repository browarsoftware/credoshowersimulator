import cv2
import numpy as np
from pyparsing import line

file_name = "8detectors"
file1 = open('d:\\Projects\\Python\\PycharmProjects\\CREDO\\experiments\\random_test\\results\\' + file_name + '.csv', 'r')
Lines = file1.readlines()

count = 0
# Strips the newline character
sizeX = 1600
sizeY = 1600
image = np.zeros((sizeX, sizeY))
for line in Lines:
    if count > 0:
        print(count)
        linesplit = line.split(',')
        x = float(linesplit[1])
        y = float(linesplit[2])
        w = float(linesplit[3])
        h = float(linesplit[4])
        back = float(linesplit[5])
        hit = float(linesplit[6])

        x_left = x - (w / 2.0)
        x_right = x + (w / 2.0)
        y_bottom = y - (h / 2.0)
        y_top = y + (h / 2.0)
        x_start = int((sizeX / 2.0) + 100.0 * x_left)
        x_stop = int((sizeX / 2.0) + 100.0 * x_right)

        y_start = int((sizeY / 2.0) + 100.0 * y_bottom)
        y_stop = int((sizeY / 2.0) + 100.0 * y_top)

        for a in range(x_start, x_stop):
            for b in range(y_start, y_stop):
                if a > 0 and a < sizeX and b > 0 and b < sizeY:
                    x_coord = a;
                    y_coord = sizeY - b - 1;
                    #image[a,b] = back + hit
                    image[y_coord, x_coord] = back + hit
    count = count + 1

print("min=" + str(np.min(image)) + ", max=" + str(np.max(image)))
image = cv2.GaussianBlur(image,(251,251),0)
#image = cv2.GaussianBlur(image,(501,501),0)
image = cv2.resize(image, (160, 160))
image = 255.0 * (image / np.max(image))

image = image.astype(np.uint8)
im_color = cv2.applyColorMap(image, cv2.COLORMAP_HOT)
#cv2.imshow("img", im_color)
#cv2.waitKey()
cv2.imwrite(file_name + ".png", im_color)
