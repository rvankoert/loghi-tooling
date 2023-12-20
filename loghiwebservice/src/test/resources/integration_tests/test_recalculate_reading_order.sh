#!/bin/bash

curl -v -X POST -F "identifier=test" -F "page=@files/recalculate_reading_order/NL-0400410000_26_005006_000381.xml" -F "border_margin=10" http://localhost:8080/recalculate-reading-order-new

result_file="/tmp/upload/test/NL-0400410000_26_005006_000381.xml"
if [ "$1" ]; then
  result_file="$1/test/NL-0400410000_26_005006_000381.xml"
fi

sleep 5

if [ -f "$result_file" ]; then
  printf "\nresult exists\n"

  number_of_indexes=$(cat < "$result_file" | grep -c "{index")

  rm "$result_file"

  if [ "$number_of_indexes" == 105 ]; then
    printf "result as expected\n"
    exit 0
  else
    printf "%s is not equal to expected number of reading indexes 105\n" "$number_of_indexes"
    exit 1
  fi
fi

printf "\n result does not exist\n"
exit 1
