#!/bin/bash

curl -v -X POST -F "identifier=test" -F "page=@files/cut_based_on_page_xml/page/NL-0400410000_26_005006_000381.xml" -F "image=@files/cut_based_on_page_xml/NL-0400410000_26_005006_000381.jpg" -F "output_type=png" -F "channels=4"  http://localhost:8080/cut-from-image-based-on-page-xml-new

result=/tmp/upload/test/NL-0400410000_26_005006_000381
if [ "$1" ]; then
  result=$1/test/NL-0400410000_26_005006_000381
fi

sleep 5

if [ -d $result ]; then

  number_of_images=$(find $result -type f | wc -l)

  rm -r $result

  if [ "$number_of_images" -eq 105 ]; then
    printf "\nCut based on page xml succeeded\n"
    exit 0
  else
    printf "\nFound %s text lines not the expected 105
    \n" "$number_of_images"
    exit 1
  fi

fi

printf "\nCould not find %s\n" "$result"
exit 1
