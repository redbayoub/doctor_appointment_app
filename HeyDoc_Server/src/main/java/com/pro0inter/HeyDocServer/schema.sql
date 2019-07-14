CREATE TABLE oauth_access_token (
token_id VARCHAR(255),
token BLOB,
authentication_id VARCHAR(255) PRIMARY KEY,
user_name VARCHAR(255),
client_id VARCHAR(255),
authentication BLOB,
refresh_token VARCHAR(255)
);
CREATE TABLE oauth_refresh_token (
token_id VARCHAR(255),
token BLOB,
authentication BLOB
);