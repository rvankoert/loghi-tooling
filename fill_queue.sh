#!/bin/bash

for i in {1..50}
do
	curl -X POST -F "identifier=id" -F "page=@/home/martijnm/workspace/images/loghi-htr/data/page/NL-0400410000_26_009015_000321.xml" -F "border_margin=200" http://localhost:8080/recalculate-reading-order-new
done
