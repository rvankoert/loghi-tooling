# Loghi tooling

This project conntains the tools used by the Loghi framework.

## Minions
These are commandline tools that help process the input and output for the Loghi framework.

### MinionCutFromImageBasedOnPageXMLNew
This tool will cut the textlines from an image based on the [PAGE](https://github.com/PRImA-Research-Lab/PAGE-XML) xml file.
It expects that the PAGE xml file has same name as the image except for the extension.

#### Show help
```bash
./target/appassembler/bin/MinionCutFromImageBasedOnPageXMLNew -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionCutFromImageBasedOnPageXMLNew -input_path /example/input_path -outputbase /example/output_path -output_type png -channels 4 -threads 5
```

This call will take the images from `/example/input_path`. 
It will take the page from `/example/input_path/page`.
The text lines will be stored as `png` in `/example/output_path` in a subfolder with the name of the original image.
The images will be stored with transparency information.
The minion will use 5 threads.

### MinionDetectLanguageOfPageXml
This minion will try to predict the language of the PAGE file.
If the PAGE file contains text regions it will also predict the language for each region.

#### Show help
```bash
./target/appassembler/bin/MinionDetectLanguageOfPageXml -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionDetectLanguageOfPageXml -page /example/page -lang_train_data /example/lang_training_data
```

`/example/page` contains the PAGE xml files of which the language has to be detected.
`-lang_train_data` is an optional argument, but it is highly recommended, to use your own training data.
The training data looks something like this:
```
lang_training_data
|
- Dutch
- English
```

`Dutch` and `English` are plain text files. 
The names should comply with https://github.com/PRImA-Research-Lab/PAGE-XML/blob/4f40cd4b68d893b02a6396cf00df3e0e96db0d21/pagecontent/schema/pagecontent.xsd#L1675
The contents of the files should be a unicode text in the language of the file name.

### MinionExtractBaseLines
This minion takes the output of [P2PaLA](https://github.com/rvankoert/P2PaLA) and uses to update the PAGE xml files of the images with the coordinates of the baselines.

#### Show help
```bash
./target/appassembler/bin/MinionExtractBaseLines -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionExtractBaseLines -input_path_png /example/p2pala/result/png -input_path_page /example/p2pala/result/png -output_path_page /example/output/page/
```
P2PaLA will output the page and pngs in the same folder.
So `input_path_png` and `input_path_page` will have the same value most of the time.

### MinionExtractBaselinesStartEndNew
This minion expects: 
 * pageXML, 
 * a folder with pngs containing the baselines
 * a folder with pngs containing the start points
 * a folder with pngs containing the end points
 * a folder to store the new PAGE xml
It extracts info about the baselines from the images and add baseline/textline information to the regions in the pagexml.
This version adds the ability to correctly detect rotated lines.

#### Show help
```bash
./target/appassembler/bin/MinionExtractBaselinesStartEndNew -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionExtractBaselinesStartEndNew3 -input_path_png /example/png_input/ -input_path_pagexml /example/page_input/ -input_path_png_start /example/input_png_start/ -input_path_png_end /example/input_png_end/ -output_path_pagexml /example/page_output/
```

### MinionExtractBaselinesStartEndNew3
This minion expects pageXML, a png containing baselines and a png containing baseline start and ending as input.
It extracts info about the baselines from the images and add baseline/textline information to the regions in the pagexml.
This version adds the ability to correctly detect rotated lines.

#### Show help
```bash
./target/appassembler/bin/MinionExtractBaselinesStartEndNew3 -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionExtractBaselinesStartEndNew3 -input_path_png /example/png_input/ -input_path_pagexml /example/page_input/ -output_path_pagexml /example/page_output/
```


### MinionGarbageCharacterCalculator
This minion returns the characters that should not be in the text as a percentage of total amount of characters.

#### Show help
```bash
./target/appassembler/bin/MinionGarbageCharacterCalculator -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionGarbageCharacterCalculator -page_file /path/to/page.xml -characters_file /path/supported_characters.txt
```

`/path/allowed_characters.txt` is a plain text file, that contains the supported without any spaces in between.
It should look something like:

```text
abcdefgABCDEFG
```


### MinionGeneratePageImages
The minion will create PAGE xml and the image, written in a font.

#### Show help
```bash
./target/appassembler/bin/MinionGeneratePageImages -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionGeneratePageImages -text_path /example/text_path/ -output_path /example/page_output/ -font_path /example/fonts 
```
* `text_path` contains  plain text files
* `font_path` contains font files (i.e. *.ttf)

For each text a synthetic will be created with a randomly chosen font.

### MinionLoghiHTRMergePageXML
This minion merges the HTR results of [Loghi HTR](https://github.com/rvankoert/loghi-htr) with the existing PAGE files.
The results file should look something like:
```
/example/image_name/page_name.xml-line_name1.png     This is a text line
/example/image_name/page_name.xml-line_name2.png     This is a text line too
```

#### Show help
```bash
./target/appassembler/bin/MinionLoghiHTRMergePageXML -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionLoghiHTRMergePageXML -input_path /example/page -results_file /example/htr_results.txt -config_file /path/to/htr-config.json
```

### MinionPyLaiaMergePageXML
This minion works similar to MinionLoghiHTRMergePageXML, but is made to process the HTR results of [Pylaia](https://github.com/jpuigcerver/PyLaia).

#### Show help
```bash
./target/appassembler/bin/MinionPyLaiaMergePageXML -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionPyLaiaMergePageXML -input_path /example/page -results_file /example/htr_results.txt
```

### MinionRecalculateReadingOrderNew
This minion will recalculate the reading order of the PAGE xml files.
It will change the page files the folder that is passed as the `input_path_page`-argument.

#### Show help
```bash
./target/appassembler/bin/MinionRecalculateReadingOrderNew -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionRecalculateReadingOrderNew -input_path_page /example/page/
```

### MinionShrinkRegions
This minion reevaluates the Text lines of the PAGE xml and shrinks them where needed.
The results are based on the image the PAGE xml is describing.

#### Show help
```bash
./target/appassembler/bin/MinionShrinkRegions -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionShrinkRegions -input /example/images
```
`input` should be a folder and should contain the images and a subfolder `page` with the PAGE xml files of the images.

### MinionShrinkTextLines
This minion reevaluates the Text lines of the PAGE xml and shrinks them where needed. 
The results are based on the image the PAGE xml is describing.

#### Show help
```bash
./target/appassembler/bin/MinionShrinkTextLines -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionShrinkTextLines -input /example/images
```
`input` should be a folder and should contain the images and a subfolder `page` with the PAGE xml files of the images. 

### MinionSplitPageXMLTextLineIntoWords
This minion will split the text lines of a PAGE xml file into words.

#### Show help
```bash
./target/appassembler/bin/MinionSplitPageXMLTextLineIntoWords -help
```

#### A typical call
```bash
./target/appassembler/bin/MinionSplitPageXMLTextLineIntoWords -input_path /example/page
```

## REST API

### Extract baselines

```bash
curl -X POST -F "mask=@/data/scratch/p2palaintermediate/5c52d146-34b1-48e8-8805-04885d39d96a.png" -F "xml=@/data/scratch/p2palaintermediate/5c52d146-34b1-48e8-8805-04885d39d96a.xml" -F "identifier=id" -F "invertImage=true" http://localhost:8080/extract-baselines
```

### CutFromImageBasedOnPageXMLNewResource

```bash
curl -X POST -F "image=@/data/scratch/p2palaintermediate/5c52d146-34b1-48e8-8805-04885d39d96a.png" -F "page=@/tmp/upload/id/extract_baselines.xml" -F "identifier=id" -F "output_type=png" -F "channels=4" http://localhost:8080/cut-from-image-based-on-page-xml-new
```

### LoghiHTRMergePageXMLResource

```bash
curl -X POST -F "identifier=id" -F "page=@/home/martijnm/workspace/images/loghi-htr/data/page/NL-0400410000_26_009015_000321.xml" -F "results=@/home/martijnm/workspace/images/loghi-htr/results.txt" -F "htr-config=@/home/martijnm/workspace/images/loghi-htr/output/config.json" http://localhost:8080/loghi-htr-merge-page-xml
```

### RecalculateReadingOrderNewResource

```bash
curl -X POST -F "identifier=id" -F "page=@/home/martijnm/workspace/images/loghi-htr/data/page/NL-0400410000_26_009015_000321.xml" -F "border_margin=200" http://localhost:8080/recalculate-reading-order-new
```

### SplitPageXMLTextLineIntoWordsResource

```bash
curl -X POST -F "identifier=id" -F "xml=@/home/stefan/Documents/repos/laypa/tutorial/data/inference/page/NL-HaNA_1.01.02_3112_0395.xml" http://localhost:8080/split-page-xml-text-line-into-words
```