#!/bin/bash

curl -v -X POST -F "identifier=test" -F "page=@files/detect_language/NL-0400410000_26_005006_000381.xml" http://localhost:8080/detect-language-of-page-xml

result_file="/tmp/upload/test/NL-0400410000_26_005006_000381.xml"
if [ "$1" ]; then
  result_file="$1/test/NL-0400410000_26_005006_000381.xml"
fi

sleep 5

if [ -f "$result_file" ]; then
  printf "\nresult exists\n"
  number_of_primary_language=$(cat $result_file | grep -c "primaryLanguage=")
  rm -r $result_file

  if [ $number_of_primary_language == 107 ]; then
    printf "number of primaryLanguage attributes is correct\n"
    exit 0
  else
    printf "%s is not equal to expected number of reading indexes 107\n" "$number_of_primary_language"
    exit 1
  fi
fi

printf "\n result does not exist\n"
exit 1