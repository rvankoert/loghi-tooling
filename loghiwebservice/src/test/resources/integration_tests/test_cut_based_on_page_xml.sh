#!/bin/bash

curl -v -X POST -F "identifier=test" -F "page=@files/cut_based_on_page_xml/page/NL-HlmNHA_1617_1682_0151.xml" -F "image=@files/cut_based_on_page_xml/NL-HlmNHA_1617_1682_0151.jpg" -F "output_type=png" -F "channels=4"  http://localhost:8080/cut-from-image-based-on-page-xml-new

result=/tmp/upload/test/NL-HlmNHA_1617_1682_0151

sleep 5

if [ -d $result ]; then

  number_of_images=$(find $result -type f | wc -l)

  rm -r $result

  if [ "$number_of_images" -eq 38 ]; then
    printf "\nCut based on page xml succeeded\n"
    exit 0
  else
    printf "\nFound %s text lines not the expected 38\n" "$number_of_images"
    exit 1
  fi

fi

printf "\nCould not find %s\n" "$result"
exit 1
