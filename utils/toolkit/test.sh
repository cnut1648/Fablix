#! /bin/bash


cat ed.sql edth.sql edrec.sql | mysql -u chris --database=mysql --password
