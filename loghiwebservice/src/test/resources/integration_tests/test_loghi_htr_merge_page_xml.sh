#!/bin/bash

curl -v -X POST -F "namespace=http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15" -F "identifier=test" -F "page=@files/loghi_htr_merge_page/page/NL-HaNA_2.05.31_1_0031.xml" -F "results=@files/loghi_htr_merge_page/results.txt" -F "htr-config=@files/loghi_htr_merge_page/htr_config.json" http://localhost:8080/loghi-htr-merge-page-xml

result_file=/tmp/upload/test/NL-HaNA_2.05.31_1_0031.xml

sleep 5

if [ -f  $result_file ]; then
  printf "\nresult exists\n"

  number_of_text_lines=$(cat $result_file | grep "<TextLine" | wc -l)

#  rm $result_file

  if [ $number_of_text_lines -eq 24 ]; then
    printf "result has expected number of text lines\n"
    exit 0
    else
        printf "%s is equal to the expected number 24" "$number_of_text_lines"
        exit 1
    fi
fi

printf "\ncould not find %s\n" "$result_file"
exit 1