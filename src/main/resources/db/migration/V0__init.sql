CREATE TABLE public.api_info (
                                 api_info_id bigserial NOT NULL,
                                 api_name varchar(255) NULL,
                                 api_host varchar(255) NULL,
                                 api_url varchar(255) NULL,
                                 content_type varchar(255) NULL,
                                 description varchar(255) NULL,
                                 http_method varchar(255) NULL,
                                 rate_limit int4 NULL,
                                 rate_limit_interval int4 NULL,
                                 api_scheme varchar(5) NULL,
                                 CONSTRAINT api_info_pkey PRIMARY KEY (api_info_id)
);

CREATE TABLE public.api_key (
                                api_key_id bigserial NOT NULL,
                                api_key varchar(255) NOT NULL,
                                key_summoner_id varchar(255) NULL,
                                use_yn bool NULL,
                                key_name varchar NULL,
                                CONSTRAINT api_key_pkey PRIMARY KEY (api_key_id)
);

CREATE TABLE public.user_info_detail (
                                         puuid varchar(255) NOT NULL,
                                         summoner_level varchar(255) NULL,
                                         account_id varchar(255) NULL,
                                         id varchar(255) NULL,
                                         "name" varchar(255) NULL,
                                         revision_date varchar(255) NULL,
                                         profile_icon_id varchar NULL,
                                         api_key_id int8 NOT NULL,
                                         created_date timestamp NULL,
                                         last_update_date timestamp NULL,
                                         CONSTRAINT user_info_detail_pkey PRIMARY KEY (puuid)
);

CREATE TABLE public."version" (
                                  "version" varchar(255) NOT NULL,
                                  current_version_yn bool NULL,
                                  update_time timestamp NULL,
                                  CONSTRAINT version_pkey PRIMARY KEY (version)
);

CREATE TABLE public.api_params (
                                   api_params_id bigserial NOT NULL,
                                   api_info_id int8 NOT NULL,
                                   data_type varchar(255) NULL,
                                   description varchar(255) NULL,
                                   is_required bool NULL,
                                   "name" varchar(255) NULL,
                                   api_key varchar NULL,
                                   api_value varchar NULL,
                                   date_param_required bool NULL DEFAULT false,
                                   CONSTRAINT api_params_pkey PRIMARY KEY (api_params_id),
                                   CONSTRAINT api_params_fk FOREIGN KEY (api_info_id) REFERENCES api_info(api_info_id)
);


CREATE TABLE public.kafka_info (
                                   id bigserial NOT NULL,
                                   topic_name varchar NULL,
                                   "partition" int4 NULL,
                                   replicas int4 NULL,
                                   api_info_id int8 NOT NULL,
                                   CONSTRAINT kafka_info_pk PRIMARY KEY (id),
                                   CONSTRAINT kafka_info_fk FOREIGN KEY (api_info_id) REFERENCES api_info(api_info_id)
);


CREATE TABLE public.match_info (
                                   id varchar NOT NULL,
                                   api_key_id int8 NOT NULL,
                                   created_date timestamp NULL,
                                   last_update_date timestamp NULL,
                                   collect_complete_yn bool NULL DEFAULT false,
                                   CONSTRAINT match_info_pk PRIMARY KEY (id),
                                   CONSTRAINT match_info_fk FOREIGN KEY (api_key_id) REFERENCES api_key(api_key_id)
);

CREATE TABLE public.user_info (
                                  summoner_id varchar(255) NOT NULL,
                                  fresh_blood bool NULL,
                                  hot_streak bool NULL,
                                  in_active bool NULL,
                                  league_id varchar(255) NULL,
                                  league_points int4 NULL,
                                  losses int4 NULL,
                                  queue_type varchar(255) NULL,
                                  "rank" varchar(255) NULL,
                                  summoner_name varchar(255) NOT NULL,
                                  tier varchar(255) NULL,
                                  veteran bool NULL,
                                  wins int4 NULL,
                                  api_key_id int8 NOT NULL,
                                  update_yn varchar NULL DEFAULT 'N'::character varying,
                                  CONSTRAINT user_info_pk PRIMARY KEY (summoner_name),
                                  CONSTRAINT user_info_fk FOREIGN KEY (api_key_id) REFERENCES api_key(api_key_id)
);