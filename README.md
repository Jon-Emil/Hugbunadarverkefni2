# HBV601G Android application

## About this project

This project is a collaborative project between four students of the university of Iceland. 

Last semester we made a SpringBoot API for a game catalog with social aspects. 

This semester and this repository is an Android application for that API. 

## Authors
Sigurður Ari Stefánsson (sas122@hi.is)

Yi Hu (yih2@hi.is)

Jón Emil Rafnsson (jer8@hi.is)
...

## Polish Backlog
* Allow changing password (snýst líka um að bæta dót við UserToUpdate klasa í endapunktunum.)
* vantar `gameId` í `ReferencedReviewDTO.java` í bakendanum svo að hægt sé að referenc-a leikinn úr reviewum í prófílum bæði eigin sínum og hjá öðrum líka. er að útfæra þetta með `AdvancedSearchParameters` eins og er en það er tæknilega ekki sú besta leið.
* `doesUserHaveGameInCollection` í `SpecificGameViewModel` er að útfæra lodik sem á heima í bakendanum.
