bioinfo-consensus > ReAligner
=============================

This program is created as a project assignment for Bioinformatics class (Faculty of Electrical Engineering and Computing, University of Zagreb).
It implements iterative realigning algorithm described in:

**ReAligner: a program for refining DNA sequence multi-alignments.**  [link](http://online.liebertpub.com/doi/abs/10.1089/cmb.1997.4.369)<br/>
*Anson EL, Myers EW.*


Run instructions
---------------------

Program requires Java installed.<br/>
Program usage - just call jar executable from terminal. <br/> 
Simple working example:
```shell
java -jar realigner.jar test_2/readsInput2.fasta test_2/layouts2.afg -e0.1 -l10
```

###**Required parameters:**

First parameter is always a path to file containing FASTA format reads and second parameter is path to file containing layout data from previous OLC phase (LAYOUT) given in AMOS/Minimus .afg format.

###**Optional:**

 - **-e** parameter stands for epsilon - expected average layout error, we recommend epsilon of 0.05-0.1
 
 - **-i** stands for maximum iteration number - we recommend max iteration of 10-15 for smaller test examples given
 
 - for further explanation please see referenced paper<br/>
 
-----------------------------------
 
You can also try with example files given in test_1, test_2 and test_3 folders.

For some testing purposes we used scripts from: https://github.com/vzupanovic/skripte<br/>
More about the algorithm, results and program itself - see documentation (coming soon)!<br/>
And also - please support our friends:<br/>
[#24hBioInfo](https://www.facebook.com/24hprojectchallenge "Check out the construction of this awesome project!")




