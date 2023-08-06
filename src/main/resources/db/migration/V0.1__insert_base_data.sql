INSERT INTO public.api_info (api_name,api_host,api_url,content_type,description,http_method,rate_limit,rate_limit_interval,api_scheme)
VALUES
('getUserEntries','kr.api.riotgames.com','/lol/league-exp/v4/entries/RANKED_SOLO_5x5/GRANDMASTER/I','json','Retrieve a list of users in the Grandmaster tier.','get',100,120,'https')
,('getUserEntries','kr.api.riotgames.com','/lol/league-exp/v4/entries/RANKED_SOLO_5x5/PLATINUM/IV','json','Retrieve a list of users in the Platinum IV tier.','get',100,120,'https')
,('getUserEntries','kr.api.riotgames.com','/lol/league-exp/v4/entries/RANKED_SOLO_5x5/PLATINUM/III','json','Retrieve a list of users in the Platinum III tier.','get',100,120,'https')
,('getUserEntries','kr.api.riotgames.com','/lol/league-exp/v4/entries/RANKED_SOLO_5x5/PLATINUM/II','json','Retrieve a list of users in the Platinum II tier.','get',100,120,'https')
,('getUserEntries','kr.api.riotgames.com','/lol/league-exp/v4/entries/RANKED_SOLO_5x5/PLATINUM/I','json','Retrieve a list of users in the Platinum I tier.','get',100,120,'https')
,('getUserEntries','kr.api.riotgames.com','/lol/league-exp/v4/entries/RANKED_SOLO_5x5/DIAMOND/IV','json','Retrieve a list of users in the Diamond IV tier.','get',100,120,'https')
,('getUserEntries','kr.api.riotgames.com','/lol/league-exp/v4/entries/RANKED_SOLO_5x5/DIAMOND/III','json','Retrieve a list of users in the Diamond III tier.','get',100,120,'https')
,('getUserEntries','kr.api.riotgames.com','/lol/league-exp/v4/entries/RANKED_SOLO_5x5/DIAMOND/II','json','Retrieve a list of users in the Diamond II tier.','get',100,120,'https')
,('getUserEntries','kr.api.riotgames.com','/lol/league-exp/v4/entries/RANKED_SOLO_5x5/DIAMOND/I','json','Retrieve a list of users in the Diamond I tier.','get',100,120,'https')
,('getUserEntries','kr.api.riotgames.com','/lol/league-exp/v4/entries/RANKED_SOLO_5x5/MASTER/I','json','Retrieve a list of users in the Master tier.','get',100,120,'https')
,('getUserEntries','kr.api.riotgames.com','/lol/league-exp/v4/entries/RANKED_SOLO_5x5/CHALLENGER/I','json','Retrieve a list of users in the Challenger tier.','get',100,120,'https')
,('getMatchList','asia.api.riotgames.com','/lol/match/v5/matches/by-puuid/{puuid}/ids','json','Retrieve a list of matches by PUUID.','get',100,120,'https')
,('getMatchDetail','asia.api.riotgames.com','/lol/match/v5/matches/{matchId}','json','Retrieve detailed match information by match ID.','get',100,120,'https')
,('getItems','ddragon.leagueoflegends.com','/cdn/{version}/data/ko_KR/item.json','json','Retrieve item information by version.','get',100,120,'https')
,('getChampions','ddragon.leagueoflegends.com','/cdn/{version}/data/ko_KR/champion.json','json','Retrieve champion information by version.','get',100,120,'https')
,('getSpells','ddragon.leagueoflegends.com','/cdn/{version}/data/ko_KR/summoner.json','json','Retrieve summoner spell information by version.','get',100,120,'https')
,('getRunes','ddragon.leagueoflegends.com','/cdn/{version}/data/ko_KR/runesReforged.json','json','Retrieve rune information by version.','get',100,120,'https')
,('getUserInfoDetail','kr.api.riotgames.com','lol/summoner/v4/summoners/{encryptedSummonerId}','json','Retrieve user information details by summoner ID to fetch match information.','get',100,120,'https')
,('getVersions','ddragon.leagueoflegends.com','/api/versions.json','json','Retrieve version information.','get',100,120,'https');

INSERT INTO public.api_params (api_info_id,data_type,description,is_required,"name",api_key,api_value,date_param_required)
VALUES
(13,'Long','Start time',true,'startTime','startTime',NULL,true)
,(13,'Long','End time',true,'endTime','endTime',NULL,true)
,(13,'String','Game type',true,'type','type','ranked',false)
,(13,'int','Page',true,'start','start','0',false)
,(13,'int','Number of items to load per page',true,'count','count','100',false);
