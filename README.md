bioinfo-consensus > ReAligner
=============================

This program is created as a project assignment for Bioinformatics class (Faculty of Electrical Engineering and Computing, University of Zagreb).
It implements iterative realigning algorithm described in:

**ReAligner: a program for refining DNA sequence multi-alignments.**
*Anson EL, Myers EW.*

Program usage example:

*java -jar realigner.jar test_2/readsInput2.fasta test_2/layouts2.afg -e(some_double_value) -l(some_integer_value)*

where -e parameter stands for epsilon - expected average layout error; and -i stands for maximum iteration number - we recommend epsilon of 0.1 and max iteration of 10 for test examples given.

For some testing purposes we used scripts from: https://github.com/vzupanovic/skripte
More about the algorithm, results and program itself - see documentation!

And also - please support:
https://www.facebook.com/24hprojectchallenge




