# SheetAnalyzer

SheetAnalyzer is a library for analyzing the depenency and formula structure of a spreadsheet. 

This branch is for TACO testing.

The main class of implementing TACO is [here](https://github.com/dataspread/sheetanalyzer/blob/tacoTest/src/main/java/org/dataspread/sheetanalyzer/dependency/DependencyGraphTACO.java)

TACO is also integrated into [Dataspread](https://github.com/dataspread/dataspread-web/tree/AsyncCompression)

[Tested_sheets](https://github.com/dataspread/sheetanalyzer/tree/tacoTest/tested_sheets) include the complex sheets we tested in the TACO paper.

The full dataset we tested in the paper is [here](https://github.com/dataspread/dataset)

## API

[SheetAnalyzer.java](https://github.com/dataspread/sheetanalyzer/blob/main/src/main/java/org/dataspread/sheetanalyzer/SheetAnalyzer.java) provides the API for use 

## Test

mvn test

