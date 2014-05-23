COPY USER_LIST
FROM 'usr_list.csv'
WITH DELIMITER ';';

COPY USR
FROM 'usr.csv'
WITH DELIMITER ';';

COPY USER_LIST_CONTAINS
FROM 'usr_list_contains.csv'
WITH DELIMITER ';';

COPY CHAT
FROM 'chat.csv'
WITH DELIMITER ';';

COPY CHAT_LIST
FROM 'chat_list.csv'
WITH DELIMITER ';';

COPY MESSAGE
	(msg_id, 
	msg_text, 
	msg_timestamp, 
	sender_login,
	chat_id)
FROM 'message.csv'
WITH DELIMITER ';';

COPY MEDIA_ATTACHMENT
FROM 'media_attachment.csv'
WITH DELIMITER ';';

COPY NOTIFICATION
FROM 'notification.csv'
WITH DELIMITER ';';