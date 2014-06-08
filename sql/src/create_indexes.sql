Create index u_log
on USR
USING BTREE
(login);

Create index u_block
on USR
Using Btree
(block_list);

create index u_con
on USR
using BTREE
(contact_list);

create index c_id
on CHAT
using BTREE
(chat_id);

create index cl_id
on CHAT
using BTREE
(chat_id);

create index m_id
on MESSAGE
using BTREE
(msg_id);
