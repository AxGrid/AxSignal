CREATE TABLE IF NOT EXISTS ax_signal(
  `channel` varchar(150),
  trx varchar(40),
  create_time datetime not null,
  done_time datetime,
  `status` int default 0 not null,
  data longtext,
  index ax_signal_status(`status`, create_time),
  index ax_signal_clean(`status`, done_time),
  index ax_signal_alarm(`status`, create_time),
  primary key (`channel`, trx)
);


