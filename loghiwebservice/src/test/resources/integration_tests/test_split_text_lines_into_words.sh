#!/bin/bash

curl -v -X POST -F "identifier=test" -F "xml=@files/split_text_lines_into_words/NL-0400410000_26_005006_000381.xml" http://localhost:8080/split-page-xml-text-line-into-words

result_file="/tmp/upload/test/NL-0400410000_26_005006_000381.xml"

if [ "$1" ]; then
  result_file="$1/test/NL-0400410000_26_005006_000381.xml"
fi


sleep 5

if [ -f $result_file ]; then
  printf "\n result exists\n"

  number_of_words=$(cat < $result_file | grep -c "<Word")

  rm $result_file

  if [ "$number_of_words" -eq 165 ]; then
    printf "result contains the expected number of words\n"
    exit 0
  else
    printf " %s is not equal to the expected number of baselines 165\n" "$number_of_words"
    exit 1
  fi

fi

printf "\n result does not exist\n"
exit 1
