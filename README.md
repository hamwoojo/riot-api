# riot-api
riot-api collect server

I created this project to collect data from the Riot API.
For information about the API, please refer to the official Riot API website.
https://developer.riotgames.com/apis

We fetch the current server version from Ddragon and collect champion information, rune information, item information, spell information, match information, and send them to Kafka.

Please refer to the Postman Collection for the API specifications.

The database for this project is built using Postgresql. 
Please refer to the SQL file for generating basic API information.

To collect data from the Riot API, an API Key is required. 
You can obtain this key from the official website. 
Additionally, since there is a rate limit, having multiple API Keys is advantageous.

By making a call to the /ddragon/version API, you can retrieve or update the version information.
After obtaining the version information,
you can make API calls to retrieve item information, spell information, champion information, and rune information to collect the respective data.

To retrieve match information, you need to follow the following procedure.

1.Call the /userinfo/user/entries API to collect user information.

2.Call the /userinfo/detail API to collect detailed user information.

3.Call the /match/list API to collect the match lists.

4.Call the /match/detail API to collect detailed information for each match.

