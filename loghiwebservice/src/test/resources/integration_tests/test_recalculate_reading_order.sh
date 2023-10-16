#!/bin/bash

curl -v -X POST -F "identifier=test" -F "page=@files/recalculate_reading_order/NL-HaNA_2.05.31_1_0031.xml" -F "border_margin=10" http://localhost:8080/recalculate-reading-order-new

result_file="/tmp/upload/test/NL-HaNA_2.05.31_1_0031.xml"

sleep 5

if [ -f $result_file ]; then
  printf "\n result exists\n"

  number_of_indexes=$(cat < $result_file | grep -c "{index")

  rm $result_file

  if [ "$number_of_indexes" == 24 ]; then
    exit 0
  else
    printf "%s is not equal to expected number of reading indexes 24" "$number_of_indexes"
    exit 1
  fi
fi

printf "\n result does not exist\n"
exit 1