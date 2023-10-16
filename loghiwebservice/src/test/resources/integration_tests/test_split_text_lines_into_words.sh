#!/bin/bash

curl -v -X POST -F "identifier=test" -F "xml=@files/cut_based_on_page_xml/page/NL-HlmNHA_1617_1682_0151.xml" http://localhost:8080/split-page-xml-text-line-into-words

result_file="/tmp/upload/test/NL-HlmNHA_1617_1682_0151.xml"

sleep 5

if [ -f $result_file ]; then
  printf "\n result exists\n"

  number_of_words=$(cat < $result_file | grep -c "<Word")

  rm $result_file

  if [ "$number_of_words" -eq 282 ]; then
    exit 0
  else
    printf " %s is not equal to the expected number of baselines 282\n" "$number_of_words"
    exit 1
  fi

fi

printf "\n result does not exist\n"
exit 1
