# Cosmic-ray showers simulator

Fast cosmic ray shower simulator.

Author: [Tomasz Hachaj](https://sppr.up.krakow.pl/hachaj/) based on Python code by Łukasz Bibrzycki 

![This is a alt text.](intro.png "Shower image")

Run a MainWindow.java file. In order to start a simulation you have to undergo following steps:
- Click "Choose experiment file" button to select an experiment configuration. You may choose already preapeed "examples/random_test/parameters.conf" file.
- Select "Simulation->Simulation plan summary" in order to view a summery of the experiment.
- Select "Simulation->Run" to start a simulation. An experiment have to have the "results" subfolder to store results.

Configuration file is a CSV with following columns:
- id - unique integer;
- th - theta, rotation cooficient;
- phi - phi, rotation cooficient; 
- r0 - shower distribution parameter;  
- offsetX, offsetY - offset of hit from middle of the simulation (in centimeters) ;
- N - number of particels of shower; 
- sampleSizeX, sampleSizeY - number of centimeters per sample grid;
- regionSizeX, regionSizeY - simulation region size (in centimeters);
- backgroundMezonsPerSquaredCentimeter - number of bacground mezons per square centimeter;
- calculateBackground, calculateHit - reserved for future, not used;
- detectorsFile - file with detectors position;
- outputDir - output directory for results, should be existing subfolder of experiment directory
- outputImageScale - if > 0 an output image of the experiment fill be produced. Should be a frction i.e. 0.1, 0.5, 0.01 etc. Too large files will take a long time to caluclate.
