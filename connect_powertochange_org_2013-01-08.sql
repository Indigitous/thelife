# ************************************************************
# Sequel Pro SQL dump
# Version 3408
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 10.182.6.232 (MySQL 5.1.61-0ubuntu0.10.10.1-log)
# Database: connect_powertochange_org
# Generation Time: 2013-01-08 14:29:47 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table actions
# ------------------------------------------------------------

CREATE TABLE `actions` (
  `aid` varchar(255) NOT NULL DEFAULT '0' COMMENT 'Primary Key: Unique actions ID.',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT 'The object that that action acts on (node, user, comment, system or custom types.)',
  `callback` varchar(255) NOT NULL DEFAULT '' COMMENT 'The callback function that executes when the action runs.',
  `parameters` longblob NOT NULL COMMENT 'Parameters to be passed to the callback function.',
  `label` varchar(255) NOT NULL DEFAULT '0' COMMENT 'Label of the action.',
  PRIMARY KEY (`aid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores action information.';



# Dump of table authmap
# ------------------------------------------------------------

CREATE TABLE `authmap` (
  `aid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key: Unique authmap ID.',
  `uid` int(11) NOT NULL DEFAULT '0' COMMENT 'Userâ€™s users.uid.',
  `authname` varchar(128) NOT NULL DEFAULT '' COMMENT 'Unique authentication name.',
  `module` varchar(128) NOT NULL DEFAULT '' COMMENT 'Module which is controlling the authentication.',
  PRIMARY KEY (`aid`),
  UNIQUE KEY `authname` (`authname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores distributed authentication mapping.';



# Dump of table batch
# ------------------------------------------------------------

CREATE TABLE `batch` (
  `bid` int(10) unsigned NOT NULL COMMENT 'Primary Key: Unique batch ID.',
  `token` varchar(64) NOT NULL COMMENT 'A string token generated against the current userâ€™s session id and the batch id, used to ensure that only the user who submitted the batch can effectively access it.',
  `timestamp` int(11) NOT NULL COMMENT 'A Unix timestamp indicating when this batch was submitted for processing. Stale batches are purged at cron time.',
  `batch` longblob COMMENT 'A serialized array containing the processing data for the batch.',
  PRIMARY KEY (`bid`),
  KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores details about batches (processes that run in...';



# Dump of table block
# ------------------------------------------------------------

CREATE TABLE `block` (
  `bid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key: Unique block ID.',
  `module` varchar(64) NOT NULL DEFAULT '' COMMENT 'The module from which the block originates; for example, â€™userâ€™ for the Whoâ€™s Online block, and â€™blockâ€™ for any custom blocks.',
  `delta` varchar(32) NOT NULL DEFAULT '0' COMMENT 'Unique ID for block within a module.',
  `theme` varchar(64) NOT NULL DEFAULT '' COMMENT 'The theme under which the block settings apply.',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Block enabled status. (1 = enabled, 0 = disabled)',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'Block weight within region.',
  `region` varchar(64) NOT NULL DEFAULT '' COMMENT 'Theme region within which the block is set.',
  `custom` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Flag to indicate how users may control visibility of the block. (0 = Users cannot control, 1 = On by default, but can be hidden, 2 = Hidden by default, but can be shown)',
  `visibility` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Flag to indicate how to show blocks on pages. (0 = Show on all pages except listed pages, 1 = Show only on listed pages, 2 = Use custom PHP code to determine visibility)',
  `pages` text NOT NULL COMMENT 'Contents of the "Pages" block; contains either a list of paths on which to include/exclude the block or PHP code, depending on "visibility" setting.',
  `title` varchar(64) NOT NULL DEFAULT '' COMMENT 'Custom title for the block. (Empty string will use block default title, <none> will remove the title, text will cause block to use specified title.)',
  `cache` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Binary flag to indicate block cache mode. (-2: Custom cache, -1: Do not cache, 1: Cache per role, 2: Cache per user, 4: Cache per page, 8: Block cache global) See DRUPAL_CACHE_* constants in ../includes/common.inc for more detailed information.',
  PRIMARY KEY (`bid`),
  UNIQUE KEY `tmd` (`theme`,`module`,`delta`),
  KEY `list` (`theme`,`status`,`region`,`weight`,`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores block settings, such as region and visibility...';



# Dump of table block_custom
# ------------------------------------------------------------

CREATE TABLE `block_custom` (
  `bid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The blockâ€™s block.bid.',
  `body` longtext COMMENT 'Block contents.',
  `info` varchar(128) NOT NULL DEFAULT '' COMMENT 'Block description.',
  `format` varchar(255) DEFAULT NULL COMMENT 'The filter_format.format of the block body.',
  PRIMARY KEY (`bid`),
  UNIQUE KEY `info` (`info`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores contents of custom-made blocks.';



# Dump of table block_node_type
# ------------------------------------------------------------

CREATE TABLE `block_node_type` (
  `module` varchar(64) NOT NULL COMMENT 'The blockâ€™s origin module, from block.module.',
  `delta` varchar(32) NOT NULL COMMENT 'The blockâ€™s unique delta within module, from block.delta.',
  `type` varchar(32) NOT NULL COMMENT 'The machine-readable name of this type from node_type.type.',
  PRIMARY KEY (`module`,`delta`,`type`),
  KEY `type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Sets up display criteria for blocks based on content types';



# Dump of table block_role
# ------------------------------------------------------------

CREATE TABLE `block_role` (
  `module` varchar(64) NOT NULL COMMENT 'The blockâ€™s origin module, from block.module.',
  `delta` varchar(32) NOT NULL COMMENT 'The blockâ€™s unique delta within module, from block.delta.',
  `rid` int(10) unsigned NOT NULL COMMENT 'The userâ€™s role ID from users_roles.rid.',
  PRIMARY KEY (`module`,`delta`,`rid`),
  KEY `rid` (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Sets up access permissions for blocks based on user roles';



# Dump of table blocked_ips
# ------------------------------------------------------------

CREATE TABLE `blocked_ips` (
  `iid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key: unique ID for IP addresses.',
  `ip` varchar(40) NOT NULL DEFAULT '' COMMENT 'IP address',
  PRIMARY KEY (`iid`),
  KEY `blocked_ip` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores blocked IP addresses.';



# Dump of table cache
# ------------------------------------------------------------

CREATE TABLE `cache` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Generic cache table for caching things not separated out...';



# Dump of table cache_block
# ------------------------------------------------------------

CREATE TABLE `cache_block` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table for the Block module to store already built...';



# Dump of table cache_bootstrap
# ------------------------------------------------------------

CREATE TABLE `cache_bootstrap` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table for data required to bootstrap Drupal, may be...';



# Dump of table cache_field
# ------------------------------------------------------------

CREATE TABLE `cache_field` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Generic cache table for caching things not separated out...';



# Dump of table cache_filter
# ------------------------------------------------------------

CREATE TABLE `cache_filter` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table for the Filter module to store already...';



# Dump of table cache_form
# ------------------------------------------------------------

CREATE TABLE `cache_form` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table for the form system to store recently built...';



# Dump of table cache_image
# ------------------------------------------------------------

CREATE TABLE `cache_image` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table used to store information about image...';



# Dump of table cache_mailchimp_user
# ------------------------------------------------------------

CREATE TABLE `cache_mailchimp_user` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table for the MailChimp module to store a list...';



# Dump of table cache_menu
# ------------------------------------------------------------

CREATE TABLE `cache_menu` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table for the menu system to store router...';



# Dump of table cache_page
# ------------------------------------------------------------

CREATE TABLE `cache_page` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table used to store compressed pages for anonymous...';



# Dump of table cache_path
# ------------------------------------------------------------

CREATE TABLE `cache_path` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table for path alias lookup.';



# Dump of table cache_token
# ------------------------------------------------------------

CREATE TABLE `cache_token` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table for token information.';



# Dump of table cache_update
# ------------------------------------------------------------

CREATE TABLE `cache_update` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table for the Update module to store information...';



# Dump of table cache_views
# ------------------------------------------------------------

CREATE TABLE `cache_views` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Generic cache table for caching things not separated out...';



# Dump of table cache_views_data
# ------------------------------------------------------------

CREATE TABLE `cache_views_data` (
  `cid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique cache ID.',
  `data` longblob COMMENT 'A collection of data to cache.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry should expire, or 0 for never.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when the cache entry was created.',
  `serialized` smallint(6) NOT NULL DEFAULT '1' COMMENT 'A flag to indicate whether content is serialized (1) or not (0).',
  PRIMARY KEY (`cid`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cache table for views to store pre-rendered queries,...';



# Dump of table cas_login_data
# ------------------------------------------------------------

CREATE TABLE `cas_login_data` (
  `cas_session_id` varchar(255) NOT NULL DEFAULT '' COMMENT 'CAS session ID',
  `uid` int(10) unsigned NOT NULL COMMENT 'The users.uid associated with the CAS session.',
  PRIMARY KEY (`cas_session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores CAS session information.';



# Dump of table cas_server_tickets
# ------------------------------------------------------------

CREATE TABLE `cas_server_tickets` (
  `service` varchar(255) NOT NULL DEFAULT '',
  `ticket` varchar(255) NOT NULL DEFAULT '',
  `uid` int(10) unsigned NOT NULL,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY (`ticket`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores CAS server tickets.';



# Dump of table cas_user
# ------------------------------------------------------------

CREATE TABLE `cas_user` (
  `aid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key: Unique authmap ID.',
  `uid` int(11) NOT NULL DEFAULT '0' COMMENT 'Userâ€™s users.uid.',
  `cas_name` varchar(128) NOT NULL DEFAULT '' COMMENT 'Unique authentication name.',
  PRIMARY KEY (`aid`),
  UNIQUE KEY `cas_name` (`cas_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores CAS authentication mapping.';



# Dump of table civicrm_acl
# ------------------------------------------------------------

CREATE TABLE `civicrm_acl` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique table ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'ACL Name.',
  `deny` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Is this ACL entry Allow  (0) or Deny (1) ?',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Table of the object possessing this ACL entry (Contact, Group, or ACL Group)',
  `entity_id` int(10) unsigned DEFAULT NULL COMMENT 'ID of the object possessing this ACL',
  `operation` enum('All','View','Edit','Create','Delete','Grant','Revoke','Search') COLLATE utf8_unicode_ci NOT NULL COMMENT 'What operation does this ACL entry control?',
  `object_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'The table of the object controlled by this ACL entry',
  `object_id` int(10) unsigned DEFAULT NULL COMMENT 'The ID of the object controlled by this ACL entry',
  `acl_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'If this is a grant/revoke entry, what table are we granting?',
  `acl_id` int(10) unsigned DEFAULT NULL COMMENT 'ID of the ACL or ACL group being granted/revoked',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this property active?',
  PRIMARY KEY (`id`),
  KEY `index_acl_id` (`acl_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_acl_cache
# ------------------------------------------------------------

CREATE TABLE `civicrm_acl_cache` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique table ID',
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'Foreign Key to Contact',
  `acl_id` int(10) unsigned NOT NULL COMMENT 'Foreign Key to ACL',
  `modified_date` date DEFAULT NULL COMMENT 'When was this cache entry last modified',
  PRIMARY KEY (`id`),
  KEY `index_acl_id` (`acl_id`),
  KEY `FK_civicrm_acl_cache_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_acl_cache_acl_id` FOREIGN KEY (`acl_id`) REFERENCES `civicrm_acl` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_acl_cache_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_acl_contact_cache
# ------------------------------------------------------------

CREATE TABLE `civicrm_acl_contact_cache` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `user_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact (could be null for anon user)',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_contact',
  `operation` enum('All','View','Edit','Create','Delete','Grant','Revoke') COLLATE utf8_unicode_ci NOT NULL COMMENT 'What operation does this user have permission on?',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_user_contact_operation` (`user_id`,`contact_id`,`operation`),
  KEY `FK_civicrm_acl_contact_cache_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_acl_contact_cache_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_acl_contact_cache_user_id` FOREIGN KEY (`user_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_acl_entity_role
# ------------------------------------------------------------

CREATE TABLE `civicrm_acl_entity_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique table ID',
  `acl_role_id` int(10) unsigned NOT NULL COMMENT 'Foreign Key to ACL Role (which is an option value pair and hence an implicit FK)',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Table of the object joined to the ACL Role (Contact or Group)',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'ID of the group/contact object being joined',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this property active?',
  PRIMARY KEY (`id`),
  KEY `index_role` (`acl_role_id`),
  KEY `index_entity` (`entity_table`,`entity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_action_log
# ------------------------------------------------------------

CREATE TABLE `civicrm_action_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'FK to id of the entity that the action was performed on. Pseudo - FK.',
  `entity_table` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'name of the entity table for the above id, e.g. civicrm_activity, civicrm_participant',
  `action_schedule_id` int(10) unsigned NOT NULL COMMENT 'FK to the action schedule that this action originated from.',
  `action_date_time` datetime DEFAULT NULL COMMENT 'date time that the action was performed on.',
  `is_error` tinyint(4) DEFAULT '0' COMMENT 'Was there any error sending the reminder?',
  `message` text COLLATE utf8_unicode_ci COMMENT 'Description / text in case there was an error encountered.',
  `repetition_number` int(10) unsigned DEFAULT NULL COMMENT 'Keeps track of the sequence number of this repetition.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_action_log_contact_id` (`contact_id`),
  KEY `FK_civicrm_action_log_action_schedule_id` (`action_schedule_id`),
  CONSTRAINT `FK_civicrm_action_log_action_schedule_id` FOREIGN KEY (`action_schedule_id`) REFERENCES `civicrm_action_schedule` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_action_log_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_action_mapping
# ------------------------------------------------------------

CREATE TABLE `civicrm_action_mapping` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `entity` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity for which the reminder is created',
  `entity_value` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity value',
  `entity_value_label` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity value label',
  `entity_status` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity status',
  `entity_status_label` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity status label',
  `entity_date_start` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity date',
  `entity_date_end` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity date',
  `entity_recipient` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity recipient',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_action_schedule
# ------------------------------------------------------------

CREATE TABLE `civicrm_action_schedule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of the action(reminder)',
  `title` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title of the action(reminder)',
  `recipient` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Recipient',
  `entity_value` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity value',
  `entity_status` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity status',
  `start_action_offset` int(10) unsigned DEFAULT NULL COMMENT 'Reminder Interval.',
  `start_action_unit` enum('hour','day','week','month','year') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Time units for reminder.',
  `start_action_condition` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Reminder Action',
  `start_action_date` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity date',
  `is_repeat` tinyint(4) DEFAULT '0',
  `repetition_frequency_unit` enum('hour','day','week','month','year') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Time units for repetition of reminder.',
  `repetition_frequency_interval` int(10) unsigned DEFAULT NULL COMMENT 'Time interval for repeating the reminder.',
  `end_frequency_unit` enum('hour','day','week','month','year') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Time units till repetition of reminder.',
  `end_frequency_interval` int(10) unsigned DEFAULT NULL COMMENT 'Time interval till repeating the reminder.',
  `end_action` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Reminder Action till repeating the reminder.',
  `end_date` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity end date',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this option active?',
  `recipient_manual` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Contact IDs to which reminder should be sent.',
  `body_text` longtext COLLATE utf8_unicode_ci COMMENT 'Body of the mailing in text format.',
  `body_html` longtext COLLATE utf8_unicode_ci COMMENT 'Body of the mailing in html format.',
  `subject` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Subject of mailing',
  `record_activity` tinyint(4) DEFAULT NULL COMMENT 'Record Activity for this reminder?',
  `mapping_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to mapping which is being used by this reminder',
  `group_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Group',
  `msg_template_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to the message template.',
  `absolute_date` date DEFAULT NULL COMMENT 'Date on which the reminder be sent.',
  `recipient_listing` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'listing based on recipient field.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_action_schedule_mapping_id` (`mapping_id`),
  KEY `FK_civicrm_action_schedule_group_id` (`group_id`),
  KEY `FK_civicrm_action_schedule_msg_template_id` (`msg_template_id`),
  CONSTRAINT `FK_civicrm_action_schedule_group_id` FOREIGN KEY (`group_id`) REFERENCES `civicrm_group` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_action_schedule_mapping_id` FOREIGN KEY (`mapping_id`) REFERENCES `civicrm_action_mapping` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_action_schedule_msg_template_id` FOREIGN KEY (`msg_template_id`) REFERENCES `civicrm_msg_template` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_activity
# ------------------------------------------------------------

CREATE TABLE `civicrm_activity` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique  Other Activity ID',
  `source_contact_id` int(10) unsigned DEFAULT NULL COMMENT 'Contact ID of the person scheduling or logging this Activity. Usually the authenticated user.',
  `source_record_id` int(10) unsigned DEFAULT NULL COMMENT 'Artificial FK to original transaction (e.g. contribution) IF it is not an Activity. Table can be figured out through activity_type_id, and further through component registry.',
  `activity_type_id` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'FK to civicrm_option_value.id, that has to be valid, registered activity type.',
  `subject` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'The subject/purpose/short description of the activity.',
  `activity_date_time` datetime DEFAULT NULL COMMENT 'Date and time this activity is scheduled to occur. Formerly named scheduled_date_time.',
  `duration` int(10) unsigned DEFAULT NULL COMMENT 'Planned or actual duration of activity expressed in minutes. Conglomerate of former duration_hours and duration_minutes.',
  `location` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Location of the activity (optional, open text).',
  `phone_id` int(10) unsigned DEFAULT NULL COMMENT 'Phone ID of the number called (optional - used if an existing phone number is selected).',
  `phone_number` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Phone number in case the number does not exist in the civicrm_phone table.',
  `details` text COLLATE utf8_unicode_ci COMMENT 'Details about the activity (agenda, notes, etc).',
  `status_id` int(10) unsigned DEFAULT NULL COMMENT 'ID of the status this activity is currently in. Foreign key to civicrm_option_value.',
  `priority_id` int(10) unsigned DEFAULT NULL COMMENT 'ID of the priority given to this activity. Foreign key to civicrm_option_value.',
  `parent_id` int(10) unsigned DEFAULT NULL COMMENT 'Parent meeting ID (if this is a follow-up item). This is not currently implemented',
  `is_test` tinyint(4) DEFAULT '0',
  `medium_id` int(10) unsigned DEFAULT NULL COMMENT 'Activity Medium, Implicit FK to civicrm_option_value where option_group = encounter_medium.',
  `is_auto` tinyint(4) DEFAULT '0',
  `relationship_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Relationship ID',
  `is_current_revision` tinyint(4) DEFAULT '1',
  `original_id` int(10) unsigned DEFAULT NULL COMMENT 'Activity ID of the first activity record in versioning chain.',
  `result` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Currently being used to store result id for survey activity, FK to option value.',
  `is_deleted` tinyint(4) DEFAULT '0',
  `campaign_id` int(10) unsigned DEFAULT NULL COMMENT 'The campaign for which this activity has been triggered.',
  `engagement_level` int(10) unsigned DEFAULT NULL COMMENT 'Assign a specific level of engagement to this activity. Used for tracking constituents in ladder of engagement.',
  `weight` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `UI_source_record_id` (`source_record_id`),
  KEY `UI_activity_type_id` (`activity_type_id`),
  KEY `index_medium_id` (`medium_id`),
  KEY `index_is_current_revision` (`is_current_revision`),
  KEY `index_is_deleted` (`is_deleted`),
  KEY `FK_civicrm_activity_source_contact_id` (`source_contact_id`),
  KEY `FK_civicrm_activity_phone_id` (`phone_id`),
  KEY `FK_civicrm_activity_parent_id` (`parent_id`),
  KEY `FK_civicrm_activity_relationship_id` (`relationship_id`),
  KEY `FK_civicrm_activity_original_id` (`original_id`),
  KEY `FK_civicrm_activity_campaign_id` (`campaign_id`),
  CONSTRAINT `FK_civicrm_activity_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_activity_original_id` FOREIGN KEY (`original_id`) REFERENCES `civicrm_activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_activity_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `civicrm_activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_activity_phone_id` FOREIGN KEY (`phone_id`) REFERENCES `civicrm_phone` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_activity_relationship_id` FOREIGN KEY (`relationship_id`) REFERENCES `civicrm_relationship` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_activity_source_contact_id` FOREIGN KEY (`source_contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_activity_assignment
# ------------------------------------------------------------

CREATE TABLE `civicrm_activity_assignment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Activity assignment id',
  `activity_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to the activity for this assignment.',
  `assignee_contact_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to the contact for this assignment.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_activity_assignee_contact_id` (`assignee_contact_id`,`activity_id`),
  KEY `FK_civicrm_activity_assignment_activity_id` (`activity_id`),
  CONSTRAINT `FK_civicrm_activity_assignment_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `civicrm_activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_activity_assignment_assignee_contact_id` FOREIGN KEY (`assignee_contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_activity_target
# ------------------------------------------------------------

CREATE TABLE `civicrm_activity_target` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Activity target id',
  `activity_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to the activity for this target.',
  `target_contact_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to the contact for this target.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_activity_target_contact_id` (`target_contact_id`,`activity_id`),
  KEY `FK_civicrm_activity_target_activity_id` (`activity_id`),
  CONSTRAINT `FK_civicrm_activity_target_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `civicrm_activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_activity_target_target_contact_id` FOREIGN KEY (`target_contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_address
# ------------------------------------------------------------

CREATE TABLE `civicrm_address` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique Address ID',
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `location_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Which Location does this address belong to.',
  `is_primary` tinyint(4) DEFAULT '0' COMMENT 'Is this the primary address.',
  `is_billing` tinyint(4) DEFAULT '0' COMMENT 'Is this the billing address.',
  `street_address` varchar(96) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Concatenation of all routable street address components (prefix, street number, street name, suffix, unit number OR P.O. Box). Apps should be able to determine physical location with this data (for mapping, mail delivery, etc.).',
  `street_number` int(11) DEFAULT NULL COMMENT 'Numeric portion of address number on the street, e.g. For 112A Main St, the street_number = 112.',
  `street_number_suffix` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Non-numeric portion of address number on the street, e.g. For 112A Main St, the street_number_suffix = A',
  `street_number_predirectional` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Directional prefix, e.g. SE Main St, SE is the prefix.',
  `street_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Actual street name, excluding St, Dr, Rd, Ave, e.g. For 112 Main St, the street_name = Main.',
  `street_type` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'St, Rd, Dr, etc.',
  `street_number_postdirectional` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Directional prefix, e.g. Main St S, S is the suffix.',
  `street_unit` varchar(16) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Secondary unit designator, e.g. Apt 3 or Unit # 14, or Bldg 1200',
  `supplemental_address_1` varchar(96) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Supplemental Address Information, Line 1',
  `supplemental_address_2` varchar(96) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Supplemental Address Information, Line 2',
  `supplemental_address_3` varchar(96) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Supplemental Address Information, Line 3',
  `city` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'City, Town or Village Name.',
  `county_id` int(10) unsigned DEFAULT NULL COMMENT 'Which County does this address belong to.',
  `state_province_id` int(10) unsigned DEFAULT NULL COMMENT 'Which State_Province does this address belong to.',
  `postal_code_suffix` varchar(12) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Store the suffix, like the +4 part in the USPS system.',
  `postal_code` varchar(12) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Store both US (zip5) AND international postal codes. App is responsible for country/region appropriate validation.',
  `usps_adc` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'USPS Bulk mailing code.',
  `country_id` int(10) unsigned DEFAULT NULL COMMENT 'Which Country does this address belong to.',
  `geo_code_1` double DEFAULT NULL COMMENT 'Latitude',
  `geo_code_2` double DEFAULT NULL COMMENT 'Longitude',
  `timezone` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Timezone expressed as a UTC offset - e.g. United States CST would be written as "UTC-6".',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `master_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Address ID',
  PRIMARY KEY (`id`),
  KEY `index_location_type` (`location_type_id`),
  KEY `index_is_primary` (`is_primary`),
  KEY `index_is_billing` (`is_billing`),
  KEY `index_street_name` (`street_name`),
  KEY `index_city` (`city`),
  KEY `FK_civicrm_address_contact_id` (`contact_id`),
  KEY `FK_civicrm_address_county_id` (`county_id`),
  KEY `FK_civicrm_address_state_province_id` (`state_province_id`),
  KEY `FK_civicrm_address_country_id` (`country_id`),
  KEY `FK_civicrm_address_master_id` (`master_id`),
  CONSTRAINT `FK_civicrm_address_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_address_country_id` FOREIGN KEY (`country_id`) REFERENCES `civicrm_country` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_address_county_id` FOREIGN KEY (`county_id`) REFERENCES `civicrm_county` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_address_master_id` FOREIGN KEY (`master_id`) REFERENCES `civicrm_address` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_address_state_province_id` FOREIGN KEY (`state_province_id`) REFERENCES `civicrm_state_province` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_address_format
# ------------------------------------------------------------

CREATE TABLE `civicrm_address_format` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Address Format Id',
  `format` text COLLATE utf8_unicode_ci COMMENT 'The format of an address',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_batch
# ------------------------------------------------------------

CREATE TABLE `civicrm_batch` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique Address ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Variable name/programmatic handle for this batch.',
  `title` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Friendly Name.',
  `description` text COLLATE utf8_unicode_ci COMMENT 'Description of this batch set.',
  `created_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `created_date` datetime DEFAULT NULL COMMENT 'When was this item created',
  `modified_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `modified_date` datetime DEFAULT NULL COMMENT 'When was this item created',
  `saved_search_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Saved Search ID',
  `status_id` int(10) unsigned NOT NULL COMMENT 'fk to Batch Status options in civicrm_option_values',
  `type_id` int(10) unsigned NOT NULL COMMENT 'fk to Batch Type options in civicrm_option_values',
  `mode_id` int(10) unsigned DEFAULT NULL COMMENT 'fk to Batch mode options in civicrm_option_values',
  `total` decimal(20,2) DEFAULT NULL COMMENT 'Total amount for this batch.',
  `item_count` int(10) unsigned NOT NULL COMMENT 'Number of items in a batch.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name` (`name`),
  KEY `FK_civicrm_batch_created_id` (`created_id`),
  KEY `FK_civicrm_batch_modified_id` (`modified_id`),
  KEY `FK_civicrm_batch_saved_search_id` (`saved_search_id`),
  CONSTRAINT `FK_civicrm_batch_created_id` FOREIGN KEY (`created_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_batch_modified_id` FOREIGN KEY (`modified_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_batch_saved_search_id` FOREIGN KEY (`saved_search_id`) REFERENCES `civicrm_saved_search` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_cache
# ------------------------------------------------------------

CREATE TABLE `civicrm_cache` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `group_name` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT 'group name for cache element, useful in cleaning cache elements',
  `path` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Unique path name for cache element',
  `data` longtext COLLATE utf8_unicode_ci COMMENT 'data associated with this path',
  `component_id` int(10) unsigned DEFAULT NULL COMMENT 'Component that this menu item belongs to',
  `created_date` datetime DEFAULT NULL COMMENT 'When was the cache item created',
  `expired_date` datetime DEFAULT NULL COMMENT 'When should the cache item expire',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_group_path_date` (`group_name`,`path`,`created_date`),
  KEY `FK_civicrm_cache_component_id` (`component_id`),
  CONSTRAINT `FK_civicrm_cache_component_id` FOREIGN KEY (`component_id`) REFERENCES `civicrm_component` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_campaign
# ------------------------------------------------------------

CREATE TABLE `civicrm_campaign` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique Campaign ID.',
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of the Campaign.',
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title of the Campaign.',
  `description` text COLLATE utf8_unicode_ci COMMENT 'Full description of Campaign.',
  `start_date` datetime DEFAULT NULL COMMENT 'Date and time that Campaign starts.',
  `end_date` datetime DEFAULT NULL COMMENT 'Date and time that Campaign ends.',
  `campaign_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Campaign Type ID.Implicit FK to civicrm_option_value where option_group = campaign_type',
  `status_id` int(10) unsigned DEFAULT NULL COMMENT 'Campaign status ID.Implicit FK to civicrm_option_value where option_group = campaign_status',
  `external_identifier` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Unique trusted external ID (generally from a legacy app/datasource). Particularly useful for deduping operations.',
  `parent_id` int(10) unsigned DEFAULT NULL COMMENT 'Optional parent id for this Campaign.',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this Campaign enabled or disabled/cancelled?',
  `created_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact, who created this Campaign.',
  `created_date` datetime DEFAULT NULL COMMENT 'Date and time that Campaign was created.',
  `last_modified_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact, who recently edited this Campaign.',
  `last_modified_date` datetime DEFAULT NULL COMMENT 'Date and time that Campaign was edited last time.',
  `goal_general` text COLLATE utf8_unicode_ci COMMENT 'General goals for Campaign.',
  `goal_revenue` decimal(20,2) DEFAULT NULL COMMENT 'The target revenue for this campaign.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_external_identifier` (`external_identifier`),
  KEY `UI_campaign_type_id` (`campaign_type_id`),
  KEY `UI_campaign_status_id` (`status_id`),
  KEY `FK_civicrm_campaign_parent_id` (`parent_id`),
  KEY `FK_civicrm_campaign_created_id` (`created_id`),
  KEY `FK_civicrm_campaign_last_modified_id` (`last_modified_id`),
  CONSTRAINT `FK_civicrm_campaign_created_id` FOREIGN KEY (`created_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_campaign_last_modified_id` FOREIGN KEY (`last_modified_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_campaign_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_campaign_group
# ------------------------------------------------------------

CREATE TABLE `civicrm_campaign_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Campaign Group id.',
  `campaign_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to the activity Campaign.',
  `group_type` enum('Include','Exclude') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Type of Group.',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of table where item being referenced is stored.',
  `entity_id` int(10) unsigned DEFAULT NULL COMMENT 'Entity id of referenced table.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_campaign_group_campaign_id` (`campaign_id`),
  CONSTRAINT `FK_civicrm_campaign_group_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_case
# ------------------------------------------------------------

CREATE TABLE `civicrm_case` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique Case ID',
  `case_type_id` varchar(128) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Id of first case category.',
  `subject` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Short name of the case.',
  `start_date` date DEFAULT NULL COMMENT 'Date on which given case starts.',
  `end_date` date DEFAULT NULL COMMENT 'Date on which given case ends.',
  `details` text COLLATE utf8_unicode_ci COMMENT 'Details about the meeting (agenda, notes, etc).',
  `status_id` int(10) unsigned NOT NULL COMMENT 'Id of case status.',
  `is_deleted` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `index_case_type_id` (`case_type_id`),
  KEY `index_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_case_activity
# ------------------------------------------------------------

CREATE TABLE `civicrm_case_activity` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique case-activity association id',
  `case_id` int(10) unsigned NOT NULL COMMENT 'Case ID of case-activity association.',
  `activity_id` int(10) unsigned NOT NULL COMMENT 'Activity ID of case-activity association.',
  PRIMARY KEY (`id`),
  KEY `UI_case_activity_id` (`case_id`,`activity_id`),
  KEY `FK_civicrm_case_activity_activity_id` (`activity_id`),
  CONSTRAINT `FK_civicrm_case_activity_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `civicrm_activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_case_activity_case_id` FOREIGN KEY (`case_id`) REFERENCES `civicrm_case` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_case_contact
# ------------------------------------------------------------

CREATE TABLE `civicrm_case_contact` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique case-contact association id',
  `case_id` int(10) unsigned NOT NULL COMMENT 'Case ID of case-contact association.',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'Contact ID of contact record given case belongs to.',
  PRIMARY KEY (`id`),
  KEY `UI_case_contact_id` (`case_id`,`contact_id`),
  KEY `FK_civicrm_case_contact_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_case_contact_case_id` FOREIGN KEY (`case_id`) REFERENCES `civicrm_case` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_case_contact_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_component
# ------------------------------------------------------------

CREATE TABLE `civicrm_component` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Component ID',
  `name` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of the component.',
  `namespace` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Path to components main directory in a form of a class\nnamespace.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_contact
# ------------------------------------------------------------

CREATE TABLE `civicrm_contact` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique Contact ID',
  `contact_type` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Type of Contact.',
  `contact_sub_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'May be used to over-ride contact view and edit templates.',
  `do_not_email` tinyint(4) DEFAULT '0',
  `do_not_phone` tinyint(4) DEFAULT '0',
  `do_not_mail` tinyint(4) DEFAULT '0',
  `do_not_sms` tinyint(4) DEFAULT '0',
  `do_not_trade` tinyint(4) DEFAULT '0',
  `is_opt_out` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Has the contact opted out from receiving all bulk email from the organization or site domain?',
  `legal_identifier` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'May be used for SSN, EIN/TIN, Household ID (census) or other applicable unique legal/government ID.',
  `external_identifier` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Unique trusted external ID (generally from a legacy app/datasource). Particularly useful for deduping operations.',
  `sort_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name used for sorting different contact types',
  `display_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Formatted name representing preferred format for display/print/other output.',
  `nick_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Nick Name.',
  `legal_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Legal Name.',
  `image_URL` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'optional URL for preferred image (photo, logo, etc.) to display for this contact.',
  `preferred_communication_method` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'What is the preferred mode of communication.',
  `preferred_language` varchar(5) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Which language is preferred for communication. FK to languages in civicrm_option_value.',
  `preferred_mail_format` enum('Text','HTML','Both') COLLATE utf8_unicode_ci DEFAULT 'Both' COMMENT 'What is the preferred mode of sending an email.',
  `hash` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Key for validating requests related to this contact.',
  `api_key` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'API Key for validating requests related to this contact.',
  `source` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'where contact come from, e.g. import, donate module insert...',
  `first_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'First Name.',
  `middle_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Middle Name.',
  `last_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Last Name.',
  `prefix_id` int(10) unsigned DEFAULT NULL COMMENT 'Prefix or Title for name (Ms, Mr...). FK to prefix ID',
  `suffix_id` int(10) unsigned DEFAULT NULL COMMENT 'Suffix for name (Jr, Sr...). FK to suffix ID',
  `email_greeting_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_option_value.id, that has to be valid registered Email Greeting.',
  `email_greeting_custom` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Custom Email Greeting.',
  `email_greeting_display` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Cache Email Greeting.',
  `postal_greeting_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_option_value.id, that has to be valid registered Postal Greeting.',
  `postal_greeting_custom` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Custom Postal greeting.',
  `postal_greeting_display` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Cache Postal greeting.',
  `addressee_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_option_value.id, that has to be valid registered Addressee.',
  `addressee_custom` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Custom Addressee.',
  `addressee_display` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Cache Addressee.',
  `job_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Job Title',
  `gender_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to gender ID',
  `birth_date` date DEFAULT NULL COMMENT 'Date of birth',
  `is_deceased` tinyint(4) DEFAULT '0',
  `deceased_date` date DEFAULT NULL COMMENT 'Date of deceased',
  `household_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Household Name.',
  `primary_contact_id` int(10) unsigned DEFAULT NULL COMMENT 'Optional FK to Primary Contact for this household.',
  `organization_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Organization Name.',
  `sic_code` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Standard Industry Classification Code.',
  `user_unique_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'the OpenID (or OpenID-style http://username.domain/) unique identifier for this contact mainly used for logging in to CiviCRM',
  `employer_id` int(10) unsigned DEFAULT NULL COMMENT 'OPTIONAL FK to civicrm_contact record.',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_external_identifier` (`external_identifier`),
  KEY `index_contact_type` (`contact_type`),
  KEY `index_contact_sub_type` (`contact_sub_type`),
  KEY `index_sort_name` (`sort_name`),
  KEY `index_preferred_communication_method` (`preferred_communication_method`),
  KEY `index_hash` (`hash`),
  KEY `index_api_key` (`api_key`),
  KEY `index_first_name` (`first_name`),
  KEY `index_last_name` (`last_name`),
  KEY `UI_prefix` (`prefix_id`),
  KEY `UI_suffix` (`suffix_id`),
  KEY `UI_gender` (`gender_id`),
  KEY `index_household_name` (`household_name`),
  KEY `index_organization_name` (`organization_name`),
  KEY `index_is_deleted` (`is_deleted`),
  KEY `FK_civicrm_contact_primary_contact_id` (`primary_contact_id`),
  KEY `FK_civicrm_contact_employer_id` (`employer_id`),
  CONSTRAINT `FK_civicrm_contact_employer_id` FOREIGN KEY (`employer_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_contact_primary_contact_id` FOREIGN KEY (`primary_contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_contact_type
# ------------------------------------------------------------

CREATE TABLE `civicrm_contact_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Contact Type ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Internal name of Contact Type (or Subtype).',
  `label` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'localized Name of Contact Type.',
  `description` text COLLATE utf8_unicode_ci COMMENT 'localized Optional verbose description of the type.',
  `image_URL` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'URL of image if any.',
  `parent_id` int(10) unsigned DEFAULT NULL COMMENT 'Optional FK to parent contact type.',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this entry active?',
  `is_reserved` tinyint(4) DEFAULT NULL COMMENT 'Is this contact type a predefined system type',
  PRIMARY KEY (`id`),
  UNIQUE KEY `contact_type` (`name`),
  KEY `FK_civicrm_contact_type_parent_id` (`parent_id`),
  CONSTRAINT `FK_civicrm_contact_type_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `civicrm_contact_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_contribution
# ------------------------------------------------------------

CREATE TABLE `civicrm_contribution` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Contribution ID',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'FK to Contact ID',
  `contribution_type_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contribution Type',
  `contribution_page_id` int(10) unsigned DEFAULT NULL COMMENT 'The Contribution Page which triggered this contribution',
  `payment_instrument_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Payment Instrument',
  `receive_date` datetime DEFAULT NULL COMMENT 'when was gift received',
  `non_deductible_amount` decimal(20,2) DEFAULT '0.00' COMMENT 'Portion of total amount which is NOT tax deductible. Equal to total_amount for non-deductible contribution types.',
  `total_amount` decimal(20,2) NOT NULL COMMENT 'Total amount of this contribution. Use market value for non-monetary gifts.',
  `fee_amount` decimal(20,2) DEFAULT NULL COMMENT 'actual processor fee if known - may be 0.',
  `net_amount` decimal(20,2) DEFAULT NULL COMMENT 'actual funds transfer amount. total less fees. if processor does not report actual fee during transaction, this is set to total_amount.',
  `trxn_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'unique transaction id. may be processor id, bank id + trans id, or account number + check number... depending on payment_method',
  `invoice_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'unique invoice id, system generated or passed in',
  `currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  `cancel_date` datetime DEFAULT NULL COMMENT 'when was gift cancelled',
  `cancel_reason` text COLLATE utf8_unicode_ci,
  `receipt_date` datetime DEFAULT NULL COMMENT 'when (if) receipt was sent. populated automatically for online donations w/ automatic receipting',
  `thankyou_date` datetime DEFAULT NULL COMMENT 'when (if) was donor thanked',
  `source` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Origin of this Contribution.',
  `amount_level` text COLLATE utf8_unicode_ci,
  `contribution_recur_id` int(10) unsigned DEFAULT NULL COMMENT 'Conditional foreign key to civicrm_contribution_recur id. Each contribution made in connection with a recurring contribution carries a foreign key to the recurring contribution record. This assumes we can track these processor initiated events.',
  `honor_contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to contact ID',
  `is_test` tinyint(4) DEFAULT '0',
  `is_pay_later` tinyint(4) DEFAULT '0',
  `contribution_status_id` int(10) unsigned DEFAULT '1',
  `honor_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Implicit FK to civicrm_option_value.',
  `address_id` int(10) unsigned DEFAULT NULL COMMENT 'Conditional foreign key to civicrm_address.id. We insert an address record for each contribution when we have associated billing name and address data.',
  `check_number` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `campaign_id` int(10) unsigned DEFAULT NULL COMMENT 'The campaign for which this contribution has been triggered.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_contrib_trxn_id` (`trxn_id`),
  UNIQUE KEY `UI_contrib_invoice_id` (`invoice_id`),
  KEY `UI_contrib_payment_instrument_id` (`payment_instrument_id`),
  KEY `index_contribution_status` (`contribution_status_id`),
  KEY `received_date` (`receive_date`),
  KEY `check_number` (`check_number`),
  KEY `FK_civicrm_contribution_contact_id` (`contact_id`),
  KEY `FK_civicrm_contribution_contribution_type_id` (`contribution_type_id`),
  KEY `FK_civicrm_contribution_contribution_page_id` (`contribution_page_id`),
  KEY `FK_civicrm_contribution_contribution_recur_id` (`contribution_recur_id`),
  KEY `FK_civicrm_contribution_honor_contact_id` (`honor_contact_id`),
  KEY `FK_civicrm_contribution_address_id` (`address_id`),
  KEY `FK_civicrm_contribution_campaign_id` (`campaign_id`),
  CONSTRAINT `FK_civicrm_contribution_address_id` FOREIGN KEY (`address_id`) REFERENCES `civicrm_address` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_contribution_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_contribution_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_contribution_contribution_page_id` FOREIGN KEY (`contribution_page_id`) REFERENCES `civicrm_contribution_page` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_contribution_contribution_recur_id` FOREIGN KEY (`contribution_recur_id`) REFERENCES `civicrm_contribution_recur` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_contribution_contribution_type_id` FOREIGN KEY (`contribution_type_id`) REFERENCES `civicrm_contribution_type` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_contribution_honor_contact_id` FOREIGN KEY (`honor_contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_contribution_page
# ------------------------------------------------------------

CREATE TABLE `civicrm_contribution_page` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Contribution Id',
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Contribution Page title. For top of page display',
  `intro_text` text COLLATE utf8_unicode_ci COMMENT 'Text and html allowed. Displayed below title.',
  `contribution_type_id` int(10) unsigned NOT NULL COMMENT 'default Contribution type assigned to contributions submitted via this page, e.g. Contribution, Campaign Contribution',
  `payment_processor` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Payment Processor for this contribution Page ',
  `is_credit_card_only` tinyint(4) DEFAULT '0' COMMENT 'if true - processing logic must reject transaction at confirmation stage if pay method != credit card',
  `is_monetary` tinyint(4) DEFAULT '1' COMMENT 'if true - allows real-time monetary transactions otherwise non-monetary transactions',
  `is_recur` tinyint(4) DEFAULT '0' COMMENT 'if true - allows recurring contributions, valid only for PayPal_Standard',
  `recur_frequency_unit` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Supported recurring frequency units.',
  `is_recur_interval` tinyint(4) DEFAULT '0' COMMENT 'if true - supports recurring intervals',
  `is_pay_later` tinyint(4) DEFAULT '0' COMMENT 'if true - allows the user to send payment directly to the org later',
  `pay_later_text` text COLLATE utf8_unicode_ci COMMENT 'The text displayed to the user in the main form',
  `pay_later_receipt` text COLLATE utf8_unicode_ci COMMENT 'The receipt sent to the user instead of the normal receipt text',
  `is_allow_other_amount` tinyint(4) DEFAULT '0' COMMENT 'if true, page will include an input text field where user can enter their own amount',
  `default_amount_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_option_value.',
  `min_amount` decimal(20,2) DEFAULT NULL COMMENT 'if other amounts allowed, user can configure minimum allowed.',
  `max_amount` decimal(20,2) DEFAULT NULL COMMENT 'if other amounts allowed, user can configure maximum allowed.',
  `goal_amount` decimal(20,2) DEFAULT NULL COMMENT 'The target goal for this page, allows people to build a goal meter',
  `thankyou_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title for Thank-you page (header title tag, and display at the top of the page).',
  `thankyou_text` text COLLATE utf8_unicode_ci COMMENT 'text and html allowed. displayed above result on success page',
  `thankyou_footer` text COLLATE utf8_unicode_ci COMMENT 'Text and html allowed. displayed at the bottom of the success page. Common usage is to include link(s) to other pages such as tell-a-friend, etc.',
  `is_for_organization` tinyint(4) DEFAULT '0' COMMENT 'if true, signup is done on behalf of an organization',
  `for_organization` text COLLATE utf8_unicode_ci COMMENT 'This text field is shown when is_for_organization is checked. For example - I am contributing on behalf on an organization.',
  `is_email_receipt` tinyint(4) DEFAULT '0',
  `receipt_from_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'FROM email name used for receipts generated by contributions to this contribution page.',
  `receipt_from_email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'FROM email address used for receipts generated by contributions to this contribution page.',
  `cc_receipt` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'comma-separated list of email addresses to cc each time a receipt is sent',
  `bcc_receipt` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'comma-separated list of email addresses to bcc each time a receipt is sent',
  `receipt_text` text COLLATE utf8_unicode_ci COMMENT 'text to include above standard receipt info on receipt email. emails are text-only, so do not allow html for now',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this property active?',
  `footer_text` text COLLATE utf8_unicode_ci COMMENT 'Text and html allowed. Displayed at the bottom of the first page of the contribution wizard.',
  `amount_block_is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this property active?',
  `honor_block_is_active` tinyint(4) DEFAULT NULL COMMENT 'Should this contribution have the honor  block enabled?',
  `honor_block_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title for honor block.',
  `honor_block_text` text COLLATE utf8_unicode_ci COMMENT 'text for honor block.',
  `start_date` datetime DEFAULT NULL COMMENT 'Date and time that this page starts.',
  `end_date` datetime DEFAULT NULL COMMENT 'Date and time that this page ends. May be NULL if no defined end date/time',
  `created_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact, who created this contribution page',
  `created_date` datetime DEFAULT NULL COMMENT 'Date and time that contribution page was created.',
  `currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  `campaign_id` int(10) unsigned DEFAULT NULL COMMENT 'The campaign for which we are collecting contributions with this page.',
  `is_share` tinyint(4) DEFAULT '1' COMMENT 'Can people share the contribution page through social media?',
  `is_confirm_enabled` tinyint(4) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_contribution_page_contribution_type_id` (`contribution_type_id`),
  KEY `FK_civicrm_contribution_page_created_id` (`created_id`),
  KEY `FK_civicrm_contribution_page_campaign_id` (`campaign_id`),
  CONSTRAINT `FK_civicrm_contribution_page_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_contribution_page_contribution_type_id` FOREIGN KEY (`contribution_type_id`) REFERENCES `civicrm_contribution_type` (`id`),
  CONSTRAINT `FK_civicrm_contribution_page_created_id` FOREIGN KEY (`created_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_contribution_product
# ------------------------------------------------------------

CREATE TABLE `civicrm_contribution_product` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int(10) unsigned NOT NULL,
  `contribution_id` int(10) unsigned NOT NULL,
  `product_option` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Option value selected if applicable - e.g. color, size etc.',
  `quantity` int(11) DEFAULT NULL,
  `fulfilled_date` date DEFAULT NULL COMMENT 'Optional. Can be used to record the date this product was fulfilled or shipped.',
  `start_date` date DEFAULT NULL COMMENT 'Actual start date for a time-delimited premium (subscription, service or membership)',
  `end_date` date DEFAULT NULL COMMENT 'Actual end date for a time-delimited premium (subscription, service or membership)',
  `comment` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_contribution_product_contribution_id` (`contribution_id`),
  CONSTRAINT `FK_civicrm_contribution_product_contribution_id` FOREIGN KEY (`contribution_id`) REFERENCES `civicrm_contribution` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_contribution_recur
# ------------------------------------------------------------

CREATE TABLE `civicrm_contribution_recur` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Contribution Recur ID',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to civicrm_contact.id .',
  `amount` decimal(20,2) NOT NULL COMMENT 'Amount to be contributed or charged each recurrence.',
  `currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  `frequency_unit` enum('day','week','month','year') COLLATE utf8_unicode_ci DEFAULT 'month' COMMENT 'Time units for recurrence of payment.',
  `frequency_interval` int(10) unsigned NOT NULL COMMENT 'Number of time units for recurrence of payment.',
  `installments` int(10) unsigned DEFAULT NULL COMMENT 'Total number of payments to be made. Set this to 0 if this is an open-ended commitment i.e. no set end date.',
  `start_date` datetime NOT NULL COMMENT 'The date the first scheduled recurring contribution occurs.',
  `create_date` datetime NOT NULL COMMENT 'When this recurring contribution record was created.',
  `modified_date` datetime DEFAULT NULL COMMENT 'Last updated date for this record. mostly the last time a payment was received',
  `cancel_date` datetime DEFAULT NULL COMMENT 'Date this recurring contribution was cancelled by contributor- if we can get access to it',
  `end_date` datetime DEFAULT NULL COMMENT 'Date this recurring contribution finished successfully',
  `processor_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Possibly needed to store a unique identifier for this recurring payment order - if this is available from the processor??',
  `trxn_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'unique transaction id. may be processor id, bank id + trans id, or account number + check number... depending on payment_method',
  `invoice_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'unique invoice id, system generated or passed in',
  `contribution_status_id` int(10) unsigned DEFAULT '1',
  `is_test` tinyint(4) DEFAULT '0',
  `cycle_day` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'Day in the period when the payment should be charged e.g. 1st of month, 15th etc.',
  `next_sched_contribution` datetime DEFAULT NULL COMMENT 'At Groundspring this was used by the cron job which triggered payments. If we''re not doing that but we know about payments, it might still be useful to store for display to org andor contributors.',
  `failure_count` int(10) unsigned DEFAULT '0' COMMENT 'Number of failed charge attempts since last success. Business rule could be set to deactivate on more than x failures.',
  `failure_retry_date` datetime DEFAULT NULL COMMENT 'At Groundspring we set a business rule to retry failed payments every 7 days - and stored the next scheduled attempt date there.',
  `auto_renew` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Some systems allow contributor to set a number of installments - but then auto-renew the subscription or commitment if they do not cancel.',
  `payment_processor_id` int(10) unsigned DEFAULT NULL COMMENT 'Foreign key to civicrm_payment_processor.id',
  `is_email_receipt` tinyint(4) DEFAULT NULL COMMENT 'if true, receipt is automatically emailed to contact on each successful payment',
  `contribution_type_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contribution Type',
  `payment_instrument_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Payment Instrument',
  `campaign_id` int(10) unsigned DEFAULT NULL COMMENT 'The campaign for which this contribution has been triggered.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_contrib_trxn_id` (`trxn_id`),
  UNIQUE KEY `UI_contrib_invoice_id` (`invoice_id`),
  KEY `index_contribution_status` (`contribution_status_id`),
  KEY `FK_civicrm_contribution_recur_contact_id` (`contact_id`),
  KEY `FK_civicrm_contribution_recur_payment_processor_id` (`payment_processor_id`),
  KEY `FK_civicrm_contribution_recur_contribution_type_id` (`contribution_type_id`),
  KEY `UI_contribution_recur_payment_instrument_id` (`payment_instrument_id`),
  KEY `FK_civicrm_contribution_recur_campaign_id` (`campaign_id`),
  CONSTRAINT `FK_civicrm_contribution_recur_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_contribution_recur_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_contribution_recur_contribution_type_id` FOREIGN KEY (`contribution_type_id`) REFERENCES `civicrm_contribution_type` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_contribution_recur_payment_processor_id` FOREIGN KEY (`payment_processor_id`) REFERENCES `civicrm_payment_processor` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_contribution_soft
# ------------------------------------------------------------

CREATE TABLE `civicrm_contribution_soft` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Soft Contribution ID',
  `contribution_id` int(10) unsigned NOT NULL COMMENT 'FK to contribution table.',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'FK to Contact ID',
  `amount` decimal(20,2) NOT NULL COMMENT 'Amount of this soft contribution.',
  `currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  `pcp_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_pcp.id',
  `pcp_display_in_roll` tinyint(4) DEFAULT '0',
  `pcp_roll_nickname` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pcp_personal_note` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_id` (`pcp_id`),
  KEY `FK_civicrm_contribution_soft_contribution_id` (`contribution_id`),
  KEY `FK_civicrm_contribution_soft_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_contribution_soft_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_contribution_soft_contribution_id` FOREIGN KEY (`contribution_id`) REFERENCES `civicrm_contribution` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_contribution_soft_pcp_id` FOREIGN KEY (`pcp_id`) REFERENCES `civicrm_pcp` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_contribution_type
# ------------------------------------------------------------

CREATE TABLE `civicrm_contribution_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Contribution Type ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Contribution Type Name.',
  `accounting_code` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional value for mapping contributions to accounting system codes for each type/category of contribution.',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Contribution Type Description.',
  `is_deductible` tinyint(4) DEFAULT '1' COMMENT 'Is this contribution type tax-deductible? If true, contributions of this type may be fully OR partially deductible - non-deductible amount is stored in the Contribution record.',
  `is_reserved` tinyint(4) DEFAULT NULL COMMENT 'Is this a predefined system object?',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this property active?',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_contribution_widget
# ------------------------------------------------------------

CREATE TABLE `civicrm_contribution_widget` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Contribution Id',
  `contribution_page_id` int(10) unsigned DEFAULT NULL COMMENT 'The Contribution Page which triggered this contribution',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this property active?',
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Widget title.',
  `url_logo` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'URL to Widget logo',
  `button_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Button title.',
  `about` text COLLATE utf8_unicode_ci COMMENT 'About description.',
  `url_homepage` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'URL to Homepage.',
  `color_title` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color_button` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color_bar` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color_main_text` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color_main` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color_main_bg` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color_bg` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color_about_link` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color_homepage_link` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_contribution_widget_contribution_page_id` (`contribution_page_id`),
  CONSTRAINT `FK_civicrm_contribution_widget_contribution_page_id` FOREIGN KEY (`contribution_page_id`) REFERENCES `civicrm_contribution_page` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_country
# ------------------------------------------------------------

CREATE TABLE `civicrm_country` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Country Id',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Country Name',
  `iso_code` char(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'ISO Code',
  `country_code` varchar(4) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'National prefix to be used when dialing TO this country.',
  `address_format_id` int(10) unsigned DEFAULT NULL COMMENT 'Foreign key to civicrm_address_format.id.',
  `idd_prefix` varchar(4) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'International direct dialing prefix from within the country TO another country',
  `ndd_prefix` varchar(4) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Access prefix to call within a country to a different area',
  `region_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to civicrm_worldregion.id.',
  `is_province_abbreviated` tinyint(4) DEFAULT '0' COMMENT 'Should state/province be displayed as abbreviation for contacts from this country?',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name_iso_code` (`name`,`iso_code`),
  KEY `FK_civicrm_country_address_format_id` (`address_format_id`),
  KEY `FK_civicrm_country_region_id` (`region_id`),
  CONSTRAINT `FK_civicrm_country_address_format_id` FOREIGN KEY (`address_format_id`) REFERENCES `civicrm_address_format` (`id`),
  CONSTRAINT `FK_civicrm_country_region_id` FOREIGN KEY (`region_id`) REFERENCES `civicrm_worldregion` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_county
# ------------------------------------------------------------

CREATE TABLE `civicrm_county` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'County ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of County',
  `abbreviation` varchar(4) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '2-4 Character Abbreviation of County',
  `state_province_id` int(10) unsigned NOT NULL COMMENT 'ID of State / Province that County belongs',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name_state_id` (`name`,`state_province_id`),
  KEY `FK_civicrm_county_state_province_id` (`state_province_id`),
  CONSTRAINT `FK_civicrm_county_state_province_id` FOREIGN KEY (`state_province_id`) REFERENCES `civicrm_state_province` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_currency
# ------------------------------------------------------------

CREATE TABLE `civicrm_currency` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Currency Id',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Currency Name',
  `symbol` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Currency Symbol',
  `numeric_code` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Numeric currency code',
  `full_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Full currency name',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_custom_field
# ------------------------------------------------------------

CREATE TABLE `civicrm_custom_field` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique Custom Field ID',
  `custom_group_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_custom_group.',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Variable name/programmatic handle for this group.',
  `label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Text for form field label (also friendly name for administering this custom property).',
  `data_type` enum('String','Int','Float','Money','Memo','Date','Boolean','StateProvince','Country','File','Link','ContactReference') COLLATE utf8_unicode_ci NOT NULL COMMENT 'Controls location of data storage in extended_data table.',
  `html_type` enum('Text','TextArea','Select','Multi-Select','AdvMulti-Select','Radio','CheckBox','Select Date','Select State/Province','Select Country','Multi-Select Country','Multi-Select State/Province','File','Link','RichTextEditor','Autocomplete-Select') COLLATE utf8_unicode_ci NOT NULL COMMENT 'HTML types plus several built-in extended types.',
  `default_value` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Use form_options.is_default for field_types which use options.',
  `is_required` tinyint(4) DEFAULT NULL COMMENT 'Is a value required for this property.',
  `is_searchable` tinyint(4) DEFAULT NULL COMMENT 'Is this property searchable.',
  `is_search_range` tinyint(4) DEFAULT '0' COMMENT 'Is this property range searchable.',
  `weight` int(11) NOT NULL DEFAULT '1' COMMENT 'Controls field display order within an extended property group.',
  `help_pre` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display before this field.',
  `help_post` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display after this field.',
  `mask` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional format instructions for specific field types, like date types.',
  `attributes` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Store collection of type-appropriate attributes, e.g. textarea  needs rows/cols attributes',
  `javascript` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional scripting attributes for field.',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this property active?',
  `is_view` tinyint(4) DEFAULT NULL COMMENT 'Is this property set by PHP Code? A code field is viewable but not editable',
  `options_per_line` int(10) unsigned DEFAULT NULL COMMENT 'number of options per line for checkbox and radio',
  `text_length` int(10) unsigned DEFAULT NULL COMMENT 'field length if alphanumeric',
  `start_date_years` int(11) DEFAULT NULL COMMENT 'Date may be up to start_date_years years prior to the current date.',
  `end_date_years` int(11) DEFAULT NULL COMMENT 'Date may be up to end_date_years years after the current date.',
  `date_format` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'date format for custom date',
  `time_format` int(10) unsigned DEFAULT NULL COMMENT 'time format for custom date',
  `note_columns` int(10) unsigned DEFAULT NULL COMMENT ' Number of columns in Note Field ',
  `note_rows` int(10) unsigned DEFAULT NULL COMMENT ' Number of rows in Note Field ',
  `column_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of the column that holds the values for this field.',
  `option_group_id` int(10) unsigned DEFAULT NULL COMMENT 'For elements with options, the option group id that is used',
  `filter` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Stores Contact Get API params contact reference custom fields. May be used for other filters in the future.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_label_custom_group_id` (`label`,`custom_group_id`),
  KEY `FK_civicrm_custom_field_custom_group_id` (`custom_group_id`),
  CONSTRAINT `FK_civicrm_custom_field_custom_group_id` FOREIGN KEY (`custom_group_id`) REFERENCES `civicrm_custom_group` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_custom_group
# ------------------------------------------------------------

CREATE TABLE `civicrm_custom_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique Custom Group ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Variable name/programmatic handle for this group.',
  `title` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Friendly Name.',
  `extends` varchar(255) COLLATE utf8_unicode_ci DEFAULT 'Contact' COMMENT 'Type of object this group extends (can add other options later e.g. contact_address, etc.).',
  `extends_entity_column_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_option_value.id (for option group custom_data_type.)',
  `extends_entity_column_value` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'linking custom group for dynamic object.',
  `style` enum('Tab','Inline') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Visual relationship between this form and its parent.',
  `collapse_display` int(10) unsigned DEFAULT '0' COMMENT 'Will this group be in collapsed or expanded mode on initial display ?',
  `help_pre` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display before fields in form.',
  `help_post` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display after fields in form.',
  `weight` int(11) NOT NULL DEFAULT '1' COMMENT 'Controls display order when multiple extended property groups are setup for the same class.',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this property active?',
  `table_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of the table that holds the values for this group.',
  `is_multiple` tinyint(4) DEFAULT NULL COMMENT 'Does this group hold multiple values?',
  `min_multiple` int(10) unsigned DEFAULT NULL COMMENT 'minimum number of multiple records (typically 0?)',
  `max_multiple` int(10) unsigned DEFAULT NULL COMMENT 'maximum number of multiple records, if 0 - no max',
  `collapse_adv_display` int(10) unsigned DEFAULT '0' COMMENT 'Will this group be in collapsed or expanded mode on advanced search display ?',
  `created_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact, who created this custom group',
  `created_date` datetime DEFAULT NULL COMMENT 'Date and time this custom group was created.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_title_extends` (`title`,`extends`),
  UNIQUE KEY `UI_name_extends` (`name`,`extends`),
  KEY `FK_civicrm_custom_group_created_id` (`created_id`),
  CONSTRAINT `FK_civicrm_custom_group_created_id` FOREIGN KEY (`created_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_dashboard
# ------------------------------------------------------------

CREATE TABLE `civicrm_dashboard` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Domain for dashboard',
  `label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'dashlet title',
  `url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'url in case of external dashlet',
  `permission` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Permission for the dashlet',
  `permission_operator` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Permission Operator',
  `column_no` tinyint(4) DEFAULT '0' COMMENT 'column no for this dashlet',
  `is_minimized` tinyint(4) DEFAULT '0' COMMENT 'Is Minimized?',
  `fullscreen_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'fullscreen url for dashlet',
  `is_fullscreen` tinyint(4) DEFAULT '1' COMMENT 'Is Fullscreen?',
  `is_active` tinyint(4) DEFAULT '0' COMMENT 'Is this dashlet active?',
  `is_reserved` tinyint(4) DEFAULT '0' COMMENT 'Is this dashlet reserved?',
  `weight` int(11) DEFAULT '0' COMMENT 'Ordering of the dashlets.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_dashboard_domain_id` (`domain_id`),
  CONSTRAINT `FK_civicrm_dashboard_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_dashboard_contact
# ------------------------------------------------------------

CREATE TABLE `civicrm_dashboard_contact` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `dashboard_id` int(10) unsigned NOT NULL COMMENT 'Dashboard ID',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'Contact ID',
  `column_no` tinyint(4) DEFAULT '0' COMMENT 'column no for this widget',
  `is_minimized` tinyint(4) DEFAULT '0' COMMENT 'Is Minimized?',
  `is_fullscreen` tinyint(4) DEFAULT '1' COMMENT 'Is Fullscreen?',
  `is_active` tinyint(4) DEFAULT '0' COMMENT 'Is this widget active?',
  `weight` int(11) DEFAULT '0' COMMENT 'Ordering of the widgets.',
  `content` longtext COLLATE utf8_unicode_ci COMMENT 'dashlet content',
  `created_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_dashboard_contact_dashboard_id` (`dashboard_id`),
  KEY `FK_civicrm_dashboard_contact_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_dashboard_contact_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_dashboard_contact_dashboard_id` FOREIGN KEY (`dashboard_id`) REFERENCES `civicrm_dashboard` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_dedupe_exception
# ------------------------------------------------------------

CREATE TABLE `civicrm_dedupe_exception` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique dedupe exception id',
  `contact_id1` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `contact_id2` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_contact_id1_contact_id2` (`contact_id1`,`contact_id2`),
  KEY `FK_civicrm_dedupe_exception_contact_id2` (`contact_id2`),
  CONSTRAINT `FK_civicrm_dedupe_exception_contact_id1` FOREIGN KEY (`contact_id1`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_dedupe_exception_contact_id2` FOREIGN KEY (`contact_id2`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_dedupe_rule
# ------------------------------------------------------------

CREATE TABLE `civicrm_dedupe_rule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique dedupe rule id',
  `dedupe_rule_group_id` int(10) unsigned NOT NULL COMMENT 'The id of the rule group this rule belongs to',
  `rule_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'The name of the table this rule is about',
  `rule_field` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'The name of the field of the table referenced in rule_table',
  `rule_length` int(10) unsigned DEFAULT NULL COMMENT 'The lenght of the matching substring',
  `rule_weight` int(11) NOT NULL COMMENT 'The weight of the rule',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_dedupe_rule_dedupe_rule_group_id` (`dedupe_rule_group_id`),
  CONSTRAINT `FK_civicrm_dedupe_rule_dedupe_rule_group_id` FOREIGN KEY (`dedupe_rule_group_id`) REFERENCES `civicrm_dedupe_rule_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_dedupe_rule_group
# ------------------------------------------------------------

CREATE TABLE `civicrm_dedupe_rule_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique dedupe rule group id',
  `contact_type` enum('Individual','Organization','Household') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'The type of contacts this group applies to',
  `threshold` int(11) NOT NULL COMMENT 'The weight threshold the sum of the rule weights has to cross to consider two contacts the same',
  `level` enum('Strict','Fuzzy') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Whether the rule should be used for cases where strict maching of the given contact type is required or a fuzzy one',
  `is_default` tinyint(4) DEFAULT NULL COMMENT 'Is this a default rule (one rule for every contact type + level combination should be default)',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of the rule group',
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Label of the rule group',
  `is_reserved` tinyint(4) DEFAULT NULL COMMENT 'Is this a reserved rule - a rule group that has been optimized and cannot be changed by the admin',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_discount
# ------------------------------------------------------------

CREATE TABLE `civicrm_discount` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'physical tablename for entity being joined to discount, e.g. civicrm_event',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'FK to entity table specified in entity_table column.',
  `option_group_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_price_set',
  `start_date` date DEFAULT NULL COMMENT 'Date when discount starts.',
  `end_date` date DEFAULT NULL COMMENT 'Date when discount ends.',
  PRIMARY KEY (`id`),
  KEY `index_entity` (`entity_table`,`entity_id`),
  KEY `index_entity_option_id` (`entity_table`,`entity_id`,`option_group_id`),
  KEY `FK_civicrm_discount_option_group_id` (`option_group_id`),
  CONSTRAINT `FK_civicrm_discount_option_group_id` FOREIGN KEY (`option_group_id`) REFERENCES `civicrm_price_set` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_domain
# ------------------------------------------------------------

CREATE TABLE `civicrm_domain` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Domain ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of Domain / Organization',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Description of Domain.',
  `config_backend` text COLLATE utf8_unicode_ci COMMENT 'Backend configuration.',
  `version` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'The civicrm version this instance is running',
  `loc_block_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Location Block ID. This is specifically not an FK to avoid circular constraints',
  `locales` text COLLATE utf8_unicode_ci COMMENT 'list of locales supported by the current db state (NULL for single-lang install)',
  `locale_custom_strings` text COLLATE utf8_unicode_ci COMMENT 'Locale specific string overrides',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_email
# ------------------------------------------------------------

CREATE TABLE `civicrm_email` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique Email ID',
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `location_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Which Location does this email belong to.',
  `email` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Email address',
  `is_primary` tinyint(4) DEFAULT '0' COMMENT 'Is this the primary?',
  `is_billing` tinyint(4) DEFAULT '0' COMMENT 'Is this the billing?',
  `on_hold` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Is this address on bounce hold?',
  `is_bulkmail` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Is this address for bulk mail ?',
  `hold_date` datetime DEFAULT NULL COMMENT 'When the address went on bounce hold',
  `reset_date` datetime DEFAULT NULL COMMENT 'When the address bounce status was last reset',
  `signature_text` text COLLATE utf8_unicode_ci COMMENT 'Text formatted signature for the email.',
  `signature_html` text COLLATE utf8_unicode_ci COMMENT 'HTML formatted signature for the email.',
  PRIMARY KEY (`id`),
  KEY `index_location_type` (`location_type_id`),
  KEY `UI_email` (`email`),
  KEY `index_is_primary` (`is_primary`),
  KEY `index_is_billing` (`is_billing`),
  KEY `FK_civicrm_email_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_email_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_entity_batch
# ------------------------------------------------------------

CREATE TABLE `civicrm_entity_batch` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'physical tablename for entity being joined to file, e.g. civicrm_contact',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'FK to entity table specified in entity_table column.',
  `batch_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_batch',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_batch_entity` (`batch_id`,`entity_id`,`entity_table`),
  KEY `index_entity` (`entity_table`,`entity_id`),
  CONSTRAINT `FK_civicrm_entity_batch_batch_id` FOREIGN KEY (`batch_id`) REFERENCES `civicrm_batch` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_entity_file
# ------------------------------------------------------------

CREATE TABLE `civicrm_entity_file` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'physical tablename for entity being joined to file, e.g. civicrm_contact',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'FK to entity table specified in entity_table column.',
  `file_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_file',
  PRIMARY KEY (`id`),
  KEY `index_entity` (`entity_table`,`entity_id`),
  KEY `index_entity_file_id` (`entity_table`,`entity_id`,`file_id`),
  KEY `FK_civicrm_entity_file_file_id` (`file_id`),
  CONSTRAINT `FK_civicrm_entity_file_file_id` FOREIGN KEY (`file_id`) REFERENCES `civicrm_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_entity_financial_trxn
# ------------------------------------------------------------

CREATE TABLE `civicrm_entity_financial_trxn` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `entity_id` int(10) unsigned NOT NULL,
  `financial_trxn_id` int(10) unsigned DEFAULT NULL,
  `amount` decimal(20,2) NOT NULL COMMENT 'allocated amount of transaction to this entity',
  `currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_entity_financial_trxn_financial_trxn_id` (`financial_trxn_id`),
  CONSTRAINT `FK_civicrm_entity_financial_trxn_financial_trxn_id` FOREIGN KEY (`financial_trxn_id`) REFERENCES `civicrm_financial_trxn` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_entity_tag
# ------------------------------------------------------------

CREATE TABLE `civicrm_entity_tag` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'physical tablename for entity being joined to file, e.g. civicrm_contact',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'FK to entity table specified in entity_table column.',
  `tag_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_tag',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_entity_id_entity_table_tag_id` (`entity_table`,`entity_id`,`tag_id`),
  KEY `FK_civicrm_entity_tag_tag_id` (`tag_id`),
  CONSTRAINT `FK_civicrm_entity_tag_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `civicrm_tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_event
# ------------------------------------------------------------

CREATE TABLE `civicrm_event` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Event',
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Event Title (e.g. Fall Fundraiser Dinner)',
  `summary` text COLLATE utf8_unicode_ci COMMENT 'Brief summary of event. Text and html allowed. Displayed on Event Registration form and can be used on other CMS pages which need an event summary.',
  `description` text COLLATE utf8_unicode_ci COMMENT 'Full description of event. Text and html allowed. Displayed on built-in Event Information screens.',
  `event_type_id` int(10) unsigned DEFAULT '0' COMMENT 'Event Type ID.Implicit FK to civicrm_option_value where option_group = event_type.',
  `participant_listing_id` int(10) unsigned DEFAULT '0' COMMENT 'Should we expose the participant list? Implicit FK to civicrm_option_value where option_group = participant_listing.',
  `is_public` tinyint(4) DEFAULT '1' COMMENT 'Public events will be included in the iCal feeds. Access to private event information may be limited using ACLs.',
  `start_date` datetime DEFAULT NULL COMMENT 'Date and time that event starts.',
  `end_date` datetime DEFAULT NULL COMMENT 'Date and time that event ends. May be NULL if no defined end date/time',
  `is_online_registration` tinyint(4) DEFAULT '0' COMMENT 'If true, include registration link on Event Info page.',
  `registration_link_text` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Text for link to Event Registration form which is displayed on Event Information screen when is_online_registration is true.',
  `registration_start_date` datetime DEFAULT NULL COMMENT 'Date and time that online registration starts.',
  `registration_end_date` datetime DEFAULT NULL COMMENT 'Date and time that online registration ends.',
  `max_participants` int(10) unsigned DEFAULT NULL COMMENT 'Maximum number of registered participants to allow. After max is reached, a custom Event Full message is displayed. If NULL, allow unlimited number of participants.',
  `event_full_text` text COLLATE utf8_unicode_ci COMMENT 'Message to display on Event Information page and INSTEAD OF Event Registration form if maximum participants are signed up. Can include email address/info about getting on a waiting list, etc. Text and html allowed.',
  `is_monetary` tinyint(4) DEFAULT '0' COMMENT 'Is this a PAID event? If true, one or more fee amounts must be set and a Payment Processor must be configured for Online Event Registration.',
  `contribution_type_id` int(10) unsigned DEFAULT '0' COMMENT 'Contribution type assigned to paid event registrations for this event. Required if is_monetary is true.',
  `payment_processor` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Payment Processor for this event ',
  `is_map` tinyint(4) DEFAULT '0' COMMENT 'Include a map block on the Event Information page when geocode info is available and a mapping provider has been specified?',
  `is_active` tinyint(4) DEFAULT '0' COMMENT 'Is this Event enabled or disabled/cancelled?',
  `fee_label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `is_show_location` tinyint(4) DEFAULT '1' COMMENT 'If true, show event location.',
  `loc_block_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Location Block ID',
  `default_role_id` int(10) unsigned DEFAULT '1' COMMENT 'Participant role ID. Implicit FK to civicrm_option_value where option_group = participant_role.',
  `intro_text` text COLLATE utf8_unicode_ci COMMENT 'Introductory message for Event Registration page. Text and html allowed. Displayed at the top of Event Registration form.',
  `footer_text` text COLLATE utf8_unicode_ci COMMENT 'Footer message for Event Registration page. Text and html allowed. Displayed at the bottom of Event Registration form.',
  `confirm_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title for Confirmation page.',
  `confirm_text` text COLLATE utf8_unicode_ci COMMENT 'Introductory message for Event Registration page. Text and html allowed. Displayed at the top of Event Registration form.',
  `confirm_footer_text` text COLLATE utf8_unicode_ci COMMENT 'Footer message for Event Registration page. Text and html allowed. Displayed at the bottom of Event Registration form.',
  `is_email_confirm` tinyint(4) DEFAULT '0' COMMENT 'If true, confirmation is automatically emailed to contact on successful registration.',
  `confirm_email_text` text COLLATE utf8_unicode_ci COMMENT 'text to include above standard event info on confirmation email. emails are text-only, so do not allow html for now',
  `confirm_from_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'FROM email name used for confirmation emails.',
  `confirm_from_email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'FROM email address used for confirmation emails.',
  `cc_confirm` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'comma-separated list of email addresses to cc each time a confirmation is sent',
  `bcc_confirm` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'comma-separated list of email addresses to bcc each time a confirmation is sent',
  `default_fee_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_option_value.',
  `default_discount_fee_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_option_value.',
  `thankyou_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title for ThankYou page.',
  `thankyou_text` text COLLATE utf8_unicode_ci COMMENT 'ThankYou Text.',
  `thankyou_footer_text` text COLLATE utf8_unicode_ci COMMENT 'Footer message.',
  `is_pay_later` tinyint(4) DEFAULT '0' COMMENT 'if true - allows the user to send payment directly to the org later',
  `pay_later_text` text COLLATE utf8_unicode_ci COMMENT 'The text displayed to the user in the main form',
  `pay_later_receipt` text COLLATE utf8_unicode_ci COMMENT 'The receipt sent to the user instead of the normal receipt text',
  `is_multiple_registrations` tinyint(4) DEFAULT '0' COMMENT 'if true - allows the user to register multiple participants for event',
  `allow_same_participant_emails` tinyint(4) DEFAULT '0' COMMENT 'if true - allows the user to register multiple registrations from same email address.',
  `has_waitlist` tinyint(4) DEFAULT NULL COMMENT 'Whether the event has waitlist support.',
  `requires_approval` tinyint(4) DEFAULT NULL COMMENT 'Whether participants require approval before they can finish registering.',
  `expiration_time` int(10) unsigned DEFAULT NULL COMMENT 'Expire pending but unconfirmed registrations after this many hours.',
  `waitlist_text` text COLLATE utf8_unicode_ci COMMENT 'Text to display when the event is full, but participants can signup for a waitlist.',
  `approval_req_text` text COLLATE utf8_unicode_ci COMMENT 'Text to display when the approval is required to complete registration for an event.',
  `is_template` tinyint(4) DEFAULT '0' COMMENT 'whether the event has template',
  `template_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Event Template Title',
  `created_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact, who created this event',
  `created_date` datetime DEFAULT NULL COMMENT 'Date and time that event was created.',
  `currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  `campaign_id` int(10) unsigned DEFAULT NULL COMMENT 'The campaign for which this event has been created.',
  `is_share` tinyint(4) DEFAULT '1' COMMENT 'Can people share the event through social media?',
  `parent_event_id` int(10) unsigned DEFAULT NULL COMMENT 'Implicit FK to civicrm_event: parent event',
  `slot_label_id` int(10) unsigned DEFAULT NULL COMMENT 'Subevent slot label. Implicit FK to civicrm_option_value where option_group = conference_slot.',
  PRIMARY KEY (`id`),
  KEY `index_event_type_id` (`event_type_id`),
  KEY `index_participant_listing_id` (`participant_listing_id`),
  KEY `FK_civicrm_event_loc_block_id` (`loc_block_id`),
  KEY `FK_civicrm_event_created_id` (`created_id`),
  KEY `FK_civicrm_event_campaign_id` (`campaign_id`),
  CONSTRAINT `FK_civicrm_event_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_event_created_id` FOREIGN KEY (`created_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_event_loc_block_id` FOREIGN KEY (`loc_block_id`) REFERENCES `civicrm_loc_block` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_event_carts
# ------------------------------------------------------------

CREATE TABLE `civicrm_event_carts` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Cart Id',
  `user_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact who created this cart',
  `coupon_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `completed` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_event_carts_user_id` (`user_id`),
  CONSTRAINT `FK_civicrm_event_carts_user_id` FOREIGN KEY (`user_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_events_in_carts
# ------------------------------------------------------------

CREATE TABLE `civicrm_events_in_carts` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Event In Cart Id',
  `event_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Event ID',
  `event_cart_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Event Cart ID',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_events_in_carts_event_id` (`event_id`),
  KEY `FK_civicrm_events_in_carts_event_cart_id` (`event_cart_id`),
  CONSTRAINT `FK_civicrm_events_in_carts_event_cart_id` FOREIGN KEY (`event_cart_id`) REFERENCES `civicrm_event_carts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_events_in_carts_event_id` FOREIGN KEY (`event_id`) REFERENCES `civicrm_event` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_extension
# ------------------------------------------------------------

CREATE TABLE `civicrm_extension` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Local Extension ID',
  `type` enum('payment','search','report','module') COLLATE utf8_unicode_ci NOT NULL,
  `full_name` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Fully qualified extension name',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Short name',
  `label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Short, printable name',
  `file` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Primary PHP file',
  `schema_version` varchar(63) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Revision code of the database schema; the format is module-defined',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this extension active?',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_extension_full_name` (`full_name`),
  KEY `UI_extension_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_file
# ------------------------------------------------------------

CREATE TABLE `civicrm_file` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique ID',
  `file_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Type of file (e.g. Transcript, Income Tax Return, etc). FK to civicrm_option_value.',
  `mime_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'mime type of the document',
  `uri` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'uri of the file on disk',
  `document` mediumblob COMMENT 'contents of the document',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Additional descriptive text regarding this attachment (optional).',
  `upload_date` datetime DEFAULT NULL COMMENT 'Date and time that this attachment was uploaded or written to server.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_financial_account
# ------------------------------------------------------------

CREATE TABLE `civicrm_financial_account` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `account_type_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_financial_trxn
# ------------------------------------------------------------

CREATE TABLE `civicrm_financial_trxn` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Gift ID',
  `from_account_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to financial_account table.',
  `to_account_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to financial_account table.',
  `trxn_date` datetime NOT NULL,
  `trxn_type` enum('Debit','Credit') COLLATE utf8_unicode_ci NOT NULL,
  `total_amount` decimal(20,2) NOT NULL COMMENT 'amount of transaction',
  `fee_amount` decimal(20,2) DEFAULT NULL COMMENT 'actual processor fee if known - may be 0.',
  `net_amount` decimal(20,2) DEFAULT NULL COMMENT 'actual funds transfer amount. total less fees. if processor does not report actual fee during transaction, this is set to total_amount.',
  `currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  `payment_processor` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'derived from Processor setting in civicrm.settings.php.',
  `trxn_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'unique processor transaction id, bank id + trans id,... depending on payment_method',
  `trxn_result_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'processor result code',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_ft_trxn_id` (`trxn_id`),
  KEY `FK_civicrm_financial_trxn_from_account_id` (`from_account_id`),
  KEY `FK_civicrm_financial_trxn_to_account_id` (`to_account_id`),
  CONSTRAINT `FK_civicrm_financial_trxn_from_account_id` FOREIGN KEY (`from_account_id`) REFERENCES `civicrm_financial_account` (`id`),
  CONSTRAINT `FK_civicrm_financial_trxn_to_account_id` FOREIGN KEY (`to_account_id`) REFERENCES `civicrm_financial_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_grant
# ------------------------------------------------------------

CREATE TABLE `civicrm_grant` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique Grant id',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'Contact ID of contact record given grant belongs to.',
  `application_received_date` date DEFAULT NULL COMMENT 'Date on which grant application was received by donor.',
  `decision_date` date DEFAULT NULL COMMENT 'Date on which grant decision was made.',
  `money_transfer_date` date DEFAULT NULL COMMENT 'Date on which grant money transfer was made.',
  `grant_due_date` date DEFAULT NULL COMMENT 'Date on which grant report is due.',
  `grant_report_received` tinyint(4) DEFAULT NULL COMMENT 'Yes/No field stating whether grant report was received by donor.',
  `grant_type_id` int(10) unsigned NOT NULL COMMENT 'Type of grant. Implicit FK to civicrm_option_value in grant_type option_group.',
  `amount_total` decimal(20,2) NOT NULL COMMENT 'Requested grant amount, in default currency.',
  `amount_requested` decimal(20,2) DEFAULT NULL COMMENT 'Requested grant amount, in original currency (optional).',
  `amount_granted` decimal(20,2) DEFAULT NULL COMMENT 'Granted amount, in default currency.',
  `currency` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  `rationale` text COLLATE utf8_unicode_ci COMMENT 'Grant rationale.',
  `status_id` int(10) unsigned NOT NULL COMMENT 'Id of Grant status.',
  PRIMARY KEY (`id`),
  KEY `index_grant_type_id` (`grant_type_id`),
  KEY `index_status_id` (`status_id`),
  KEY `FK_civicrm_grant_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_grant_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_group
# ------------------------------------------------------------

CREATE TABLE `civicrm_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Group ID',
  `name` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Internal name of Group.',
  `title` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of Group.',
  `description` text COLLATE utf8_unicode_ci COMMENT 'Optional verbose description of the group.',
  `source` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Module or process which created this group.',
  `saved_search_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to saved search table.',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this entry active?',
  `visibility` enum('User and User Admin Only','Public Pages') COLLATE utf8_unicode_ci DEFAULT 'User and User Admin Only' COMMENT 'In what context(s) is this field visible.',
  `where_clause` text COLLATE utf8_unicode_ci COMMENT 'the sql where clause if a saved search acl',
  `select_tables` text COLLATE utf8_unicode_ci COMMENT 'the tables to be included in a select data',
  `where_tables` text COLLATE utf8_unicode_ci COMMENT 'the tables to be included in the count statement',
  `group_type` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'FK to group type',
  `cache_date` datetime DEFAULT NULL COMMENT 'Date when we created the cache for a smart group',
  `parents` text COLLATE utf8_unicode_ci COMMENT 'IDs of the parent(s)',
  `children` text COLLATE utf8_unicode_ci COMMENT 'IDs of the child(ren)',
  `is_hidden` tinyint(4) DEFAULT '0' COMMENT 'Is this group hidden?',
  `is_reserved` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_title` (`title`),
  UNIQUE KEY `UI_name` (`name`),
  KEY `index_group_type` (`group_type`),
  KEY `FK_civicrm_group_saved_search_id` (`saved_search_id`),
  CONSTRAINT `FK_civicrm_group_saved_search_id` FOREIGN KEY (`saved_search_id`) REFERENCES `civicrm_saved_search` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_group_contact
# ------------------------------------------------------------

CREATE TABLE `civicrm_group_contact` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `group_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_group',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_contact',
  `status` enum('Added','Removed','Pending') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'status of contact relative to membership in group',
  `location_id` int(10) unsigned DEFAULT NULL COMMENT 'Optional location to associate with this membership',
  `email_id` int(10) unsigned DEFAULT NULL COMMENT 'Optional email to associate with this membership',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_contact_group` (`contact_id`,`group_id`),
  KEY `FK_civicrm_group_contact_group_id` (`group_id`),
  KEY `FK_civicrm_group_contact_location_id` (`location_id`),
  KEY `FK_civicrm_group_contact_email_id` (`email_id`),
  CONSTRAINT `FK_civicrm_group_contact_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_group_contact_email_id` FOREIGN KEY (`email_id`) REFERENCES `civicrm_email` (`id`),
  CONSTRAINT `FK_civicrm_group_contact_group_id` FOREIGN KEY (`group_id`) REFERENCES `civicrm_group` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_group_contact_location_id` FOREIGN KEY (`location_id`) REFERENCES `civicrm_loc_block` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_group_contact_cache
# ------------------------------------------------------------

CREATE TABLE `civicrm_group_contact_cache` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `group_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_group',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_contact',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_contact_group` (`contact_id`,`group_id`),
  KEY `FK_civicrm_group_contact_cache_group_id` (`group_id`),
  CONSTRAINT `FK_civicrm_group_contact_cache_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_group_contact_cache_group_id` FOREIGN KEY (`group_id`) REFERENCES `civicrm_group` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_group_nesting
# ------------------------------------------------------------

CREATE TABLE `civicrm_group_nesting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Relationship ID',
  `child_group_id` int(10) unsigned NOT NULL COMMENT 'ID of the child group',
  `parent_group_id` int(10) unsigned NOT NULL COMMENT 'ID of the parent group',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_group_nesting_child_group_id` (`child_group_id`),
  KEY `FK_civicrm_group_nesting_parent_group_id` (`parent_group_id`),
  CONSTRAINT `FK_civicrm_group_nesting_child_group_id` FOREIGN KEY (`child_group_id`) REFERENCES `civicrm_group` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_group_nesting_parent_group_id` FOREIGN KEY (`parent_group_id`) REFERENCES `civicrm_group` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_group_organization
# ------------------------------------------------------------

CREATE TABLE `civicrm_group_organization` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Relationship ID',
  `group_id` int(10) unsigned NOT NULL COMMENT 'ID of the group',
  `organization_id` int(10) unsigned NOT NULL COMMENT 'ID of the Organization Contact',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_group_organization` (`group_id`,`organization_id`),
  KEY `FK_civicrm_group_organization_organization_id` (`organization_id`),
  CONSTRAINT `FK_civicrm_group_organization_group_id` FOREIGN KEY (`group_id`) REFERENCES `civicrm_group` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_group_organization_organization_id` FOREIGN KEY (`organization_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_group_roles_rules
# ------------------------------------------------------------

CREATE TABLE `civicrm_group_roles_rules` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_id` int(10) unsigned NOT NULL,
  `group_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table civicrm_im
# ------------------------------------------------------------

CREATE TABLE `civicrm_im` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique IM ID',
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `location_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Which Location does this email belong to.',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'IM screen name',
  `provider_id` int(10) unsigned DEFAULT NULL COMMENT 'Which IM Provider does this screen name belong to.',
  `is_primary` tinyint(4) DEFAULT '0' COMMENT 'Is this the primary IM for this contact and location.',
  `is_billing` tinyint(4) DEFAULT '0' COMMENT 'Is this the billing?',
  PRIMARY KEY (`id`),
  KEY `index_location_type` (`location_type_id`),
  KEY `UI_provider_id` (`provider_id`),
  KEY `index_is_primary` (`is_primary`),
  KEY `index_is_billing` (`is_billing`),
  KEY `FK_civicrm_im_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_im_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_import_job_92fdb0ed34a73aa109ab07cada012ff1
# ------------------------------------------------------------

CREATE TABLE `civicrm_import_job_92fdb0ed34a73aa109ab07cada012ff1` (
  `internal_contact_id` text COLLATE utf8_unicode_ci,
  `imported` text COLLATE utf8_unicode_ci,
  `_status` varchar(32) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'NEW',
  `_statusMsg` text COLLATE utf8_unicode_ci,
  `_id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_job
# ------------------------------------------------------------

CREATE TABLE `civicrm_job` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Job Id',
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Which Domain is this scheduled job for',
  `run_frequency` enum('Hourly','Daily','Always') COLLATE utf8_unicode_ci DEFAULT 'Daily' COMMENT 'Scheduled job run frequency.',
  `last_run` datetime DEFAULT NULL COMMENT 'When was this cron entry last run',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title of the job',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Description of the job',
  `api_prefix` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Prefix of the job api call',
  `api_entity` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Entity of the job api call',
  `api_action` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Action of the job api call',
  `parameters` text COLLATE utf8_unicode_ci COMMENT 'List of parameters to the command.',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this job active?',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_job_domain_id` (`domain_id`),
  CONSTRAINT `FK_civicrm_job_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_job_log
# ------------------------------------------------------------

CREATE TABLE `civicrm_job_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Job log entry Id',
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Which Domain is this scheduled job for',
  `run_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Log entry date',
  `job_id` int(10) unsigned DEFAULT NULL COMMENT 'Pointer to job id - not a FK though, just for logging purposes',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title of the job',
  `command` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Full path to file containing job script',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title line of log entry',
  `data` text COLLATE utf8_unicode_ci COMMENT 'Potential extended data for specific job run (e.g. tracebacks).',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_job_log_domain_id` (`domain_id`),
  CONSTRAINT `FK_civicrm_job_log_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_line_item
# ------------------------------------------------------------

CREATE TABLE `civicrm_line_item` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Line Item',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'table which has the transaction',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'entry in table',
  `price_field_id` int(10) unsigned NOT NULL COMMENT 'FK to price_field',
  `label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `qty` int(10) unsigned NOT NULL COMMENT 'How many items ordered',
  `unit_price` decimal(20,2) NOT NULL COMMENT 'price of each item',
  `line_total` decimal(20,2) NOT NULL COMMENT 'qty * unit_price',
  `participant_count` int(10) unsigned DEFAULT NULL COMMENT 'Participant count for field',
  `price_field_value_id` int(10) unsigned DEFAULT NULL COMMENT 'Implicit FK to civicrm_option_value',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_line_item_value` (`entity_table`,`entity_id`,`price_field_value_id`,`price_field_id`),
  KEY `index_entity` (`entity_table`,`entity_id`),
  KEY `FK_civicrm_line_item_price_field_id` (`price_field_id`),
  KEY `FK_civicrm_line_item_price_field_value_id` (`price_field_value_id`),
  CONSTRAINT `FK_civicrm_line_item_price_field_id` FOREIGN KEY (`price_field_id`) REFERENCES `civicrm_price_field` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_line_item_price_field_value_id` FOREIGN KEY (`price_field_value_id`) REFERENCES `civicrm_price_field_value` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_loc_block
# ------------------------------------------------------------

CREATE TABLE `civicrm_loc_block` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique ID',
  `address_id` int(10) unsigned DEFAULT NULL,
  `email_id` int(10) unsigned DEFAULT NULL,
  `phone_id` int(10) unsigned DEFAULT NULL,
  `im_id` int(10) unsigned DEFAULT NULL,
  `address_2_id` int(10) unsigned DEFAULT NULL,
  `email_2_id` int(10) unsigned DEFAULT NULL,
  `phone_2_id` int(10) unsigned DEFAULT NULL,
  `im_2_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_loc_block_address_id` (`address_id`),
  KEY `FK_civicrm_loc_block_email_id` (`email_id`),
  KEY `FK_civicrm_loc_block_phone_id` (`phone_id`),
  KEY `FK_civicrm_loc_block_im_id` (`im_id`),
  KEY `FK_civicrm_loc_block_address_2_id` (`address_2_id`),
  KEY `FK_civicrm_loc_block_email_2_id` (`email_2_id`),
  KEY `FK_civicrm_loc_block_phone_2_id` (`phone_2_id`),
  KEY `FK_civicrm_loc_block_im_2_id` (`im_2_id`),
  CONSTRAINT `FK_civicrm_loc_block_address_2_id` FOREIGN KEY (`address_2_id`) REFERENCES `civicrm_address` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_loc_block_address_id` FOREIGN KEY (`address_id`) REFERENCES `civicrm_address` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_loc_block_email_2_id` FOREIGN KEY (`email_2_id`) REFERENCES `civicrm_email` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_loc_block_email_id` FOREIGN KEY (`email_id`) REFERENCES `civicrm_email` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_loc_block_im_2_id` FOREIGN KEY (`im_2_id`) REFERENCES `civicrm_im` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_loc_block_im_id` FOREIGN KEY (`im_id`) REFERENCES `civicrm_im` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_loc_block_phone_2_id` FOREIGN KEY (`phone_2_id`) REFERENCES `civicrm_phone` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_loc_block_phone_id` FOREIGN KEY (`phone_id`) REFERENCES `civicrm_phone` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_location_type
# ------------------------------------------------------------

CREATE TABLE `civicrm_location_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Location Type ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Location Type Name.',
  `display_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Location Type Display Name.',
  `vcard_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'vCard Location Type Name.',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Location Type Description.',
  `is_reserved` tinyint(4) DEFAULT NULL COMMENT 'Is this location type a predefined system location?',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this property active?',
  `is_default` tinyint(4) DEFAULT NULL COMMENT 'Is this location type the default?',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_log
# ------------------------------------------------------------

CREATE TABLE `civicrm_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Log ID',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of table where item being referenced is stored.',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to the referenced item.',
  `data` text COLLATE utf8_unicode_ci COMMENT 'Updates does to this object if any.',
  `modified_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID of person under whose credentials this data modification was made.',
  `modified_date` datetime DEFAULT NULL COMMENT 'When was the referenced entity created or modified or deleted.',
  PRIMARY KEY (`id`),
  KEY `index_entity` (`entity_table`,`entity_id`),
  KEY `FK_civicrm_log_modified_id` (`modified_id`),
  CONSTRAINT `FK_civicrm_log_modified_id` FOREIGN KEY (`modified_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mail_settings
# ------------------------------------------------------------

CREATE TABLE `civicrm_mail_settings` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Which Domain is this match entry for',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'name of this group of settings',
  `is_default` tinyint(4) DEFAULT NULL COMMENT 'whether this is the default set of settings for this domain',
  `domain` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'email address domain (the part after @)',
  `localpart` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'optional local part (like civimail+ for addresses like civimail+s.1.2@example.com)',
  `return_path` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'contents of the Return-Path header',
  `protocol` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'name of the protocol to use for polling (like IMAP, POP3 or Maildir)',
  `server` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'server to use when polling',
  `port` int(10) unsigned DEFAULT NULL COMMENT 'port to use when polling',
  `username` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'username to use when polling',
  `password` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'password to use when polling',
  `is_ssl` tinyint(4) DEFAULT NULL COMMENT 'whether to use SSL or not',
  `source` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'folder to poll from when using IMAP, path to poll from when using Maildir, etc.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `domain_id` int(10) unsigned DEFAULT NULL COMMENT 'Which site is this mailing for',
  `header_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to the header component.',
  `footer_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to the footer component.',
  `reply_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to the auto-responder component.',
  `unsubscribe_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to the unsubscribe component.',
  `resubscribe_id` int(10) unsigned DEFAULT NULL,
  `optout_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to the opt-out component.',
  `name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Mailing Name.',
  `from_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'From Header of mailing',
  `from_email` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'From Email of mailing',
  `replyto_email` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Reply-To Email of mailing',
  `subject` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Subject of mailing',
  `body_text` longtext COLLATE utf8_unicode_ci COMMENT 'Body of the mailing in text format.',
  `body_html` longtext COLLATE utf8_unicode_ci COMMENT 'Body of the mailing in html format.',
  `url_tracking` tinyint(4) DEFAULT NULL COMMENT 'Should we track URL click-throughs for this mailing?',
  `forward_replies` tinyint(4) DEFAULT NULL COMMENT 'Should we forward replies back to the author?',
  `auto_responder` tinyint(4) DEFAULT NULL COMMENT 'Should we enable the auto-responder?',
  `open_tracking` tinyint(4) DEFAULT NULL COMMENT 'Should we track when recipients open/read this mailing?',
  `is_completed` tinyint(4) DEFAULT NULL COMMENT 'Has at least one job associated with this mailing finished?',
  `msg_template_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to the message template.',
  `override_verp` tinyint(4) DEFAULT '0' COMMENT 'Should we overrite VERP address in Reply-To',
  `created_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID who first created this mailing',
  `created_date` datetime DEFAULT NULL COMMENT 'Date and time this mailing was created.',
  `scheduled_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID who scheduled this mailing',
  `scheduled_date` datetime DEFAULT NULL COMMENT 'Date and time this mailing was scheduled.',
  `approver_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID who approved this mailing',
  `approval_date` datetime DEFAULT NULL COMMENT 'Date and time this mailing was approved.',
  `approval_status_id` int(10) unsigned DEFAULT NULL COMMENT 'The status of this mailing. Values: none, approved, rejected',
  `approval_note` longtext COLLATE utf8_unicode_ci COMMENT 'Note behind the decision.',
  `is_archived` tinyint(4) DEFAULT '0' COMMENT 'Is this mailing archived?',
  `visibility` enum('User and User Admin Only','Public Pages') COLLATE utf8_unicode_ci DEFAULT 'User and User Admin Only' COMMENT 'In what context(s) is the mailing contents visible (online viewing)',
  `campaign_id` int(10) unsigned DEFAULT NULL COMMENT 'The campaign for which this mailing has been initiated.',
  `dedupe_email` tinyint(4) DEFAULT '0' COMMENT 'Remove duplicate emails?',
  `sms_provider_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_sms_provider id ',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_domain_id` (`domain_id`),
  KEY `FK_civicrm_mailing_header_id` (`header_id`),
  KEY `FK_civicrm_mailing_footer_id` (`footer_id`),
  KEY `FK_civicrm_mailing_reply_id` (`reply_id`),
  KEY `FK_civicrm_mailing_unsubscribe_id` (`unsubscribe_id`),
  KEY `FK_civicrm_mailing_optout_id` (`optout_id`),
  KEY `FK_civicrm_mailing_msg_template_id` (`msg_template_id`),
  KEY `FK_civicrm_mailing_created_id` (`created_id`),
  KEY `FK_civicrm_mailing_scheduled_id` (`scheduled_id`),
  KEY `FK_civicrm_mailing_approver_id` (`approver_id`),
  KEY `FK_civicrm_mailing_campaign_id` (`campaign_id`),
  KEY `FK_civicrm_mailing_sms_provider_id` (`sms_provider_id`),
  CONSTRAINT `FK_civicrm_mailing_approver_id` FOREIGN KEY (`approver_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_created_id` FOREIGN KEY (`created_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_footer_id` FOREIGN KEY (`footer_id`) REFERENCES `civicrm_mailing_component` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_header_id` FOREIGN KEY (`header_id`) REFERENCES `civicrm_mailing_component` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_msg_template_id` FOREIGN KEY (`msg_template_id`) REFERENCES `civicrm_msg_template` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_optout_id` FOREIGN KEY (`optout_id`) REFERENCES `civicrm_mailing_component` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_reply_id` FOREIGN KEY (`reply_id`) REFERENCES `civicrm_mailing_component` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_scheduled_id` FOREIGN KEY (`scheduled_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_sms_provider_id` FOREIGN KEY (`sms_provider_id`) REFERENCES `civicrm_sms_provider` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_unsubscribe_id` FOREIGN KEY (`unsubscribe_id`) REFERENCES `civicrm_mailing_component` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_bounce_pattern
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_bounce_pattern` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `bounce_type_id` int(10) unsigned NOT NULL COMMENT 'Type of bounce',
  `pattern` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'A regexp to match a message to a bounce type',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_bounce_pattern_bounce_type_id` (`bounce_type_id`),
  CONSTRAINT `FK_civicrm_mailing_bounce_pattern_bounce_type_id` FOREIGN KEY (`bounce_type_id`) REFERENCES `civicrm_mailing_bounce_type` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_bounce_type
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_bounce_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` enum('AOL','Away','DNS','Host','Inactive','Invalid','Loop','Quota','Relay','Spam','Syntax','Unknown','Mandrill Hard','Mandrill Soft','Mandrill Spam','Mandrill Reject') COLLATE utf8_unicode_ci NOT NULL COMMENT 'Type of bounce',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'A description of this bounce type',
  `hold_threshold` int(10) unsigned NOT NULL COMMENT 'Number of bounces of this type required before the email address is put on bounce hold',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_component
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_component` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'The name of this component',
  `component_type` enum('Header','Footer','Subscribe','Welcome','Unsubscribe','OptOut','Reply','Resubscribe') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Type of Component.',
  `subject` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `body_html` text COLLATE utf8_unicode_ci COMMENT 'Body of the component in html format.',
  `body_text` text COLLATE utf8_unicode_ci COMMENT 'Body of the component in text format.',
  `is_default` tinyint(4) DEFAULT '0' COMMENT 'Is this the default component for this component_type?',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this property active?',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_event_bounce
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_event_bounce` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_queue_id` int(10) unsigned NOT NULL COMMENT 'FK to EventQueue',
  `bounce_type_id` int(10) unsigned DEFAULT NULL COMMENT 'What type of bounce was it?',
  `bounce_reason` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'The reason the email bounced.',
  `time_stamp` datetime NOT NULL COMMENT 'When this bounce event occurred.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_event_bounce_event_queue_id` (`event_queue_id`),
  CONSTRAINT `FK_civicrm_mailing_event_bounce_event_queue_id` FOREIGN KEY (`event_queue_id`) REFERENCES `civicrm_mailing_event_queue` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_event_confirm
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_event_confirm` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_subscribe_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_mailing_event_subscribe',
  `time_stamp` datetime NOT NULL COMMENT 'When this confirmation event occurred.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_event_confirm_event_subscribe_id` (`event_subscribe_id`),
  CONSTRAINT `FK_civicrm_mailing_event_confirm_event_subscribe_id` FOREIGN KEY (`event_subscribe_id`) REFERENCES `civicrm_mailing_event_subscribe` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_event_delivered
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_event_delivered` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_queue_id` int(10) unsigned NOT NULL COMMENT 'FK to EventQueue',
  `time_stamp` datetime NOT NULL COMMENT 'When this delivery event occurred.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_event_delivered_event_queue_id` (`event_queue_id`),
  CONSTRAINT `FK_civicrm_mailing_event_delivered_event_queue_id` FOREIGN KEY (`event_queue_id`) REFERENCES `civicrm_mailing_event_queue` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_event_forward
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_event_forward` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_queue_id` int(10) unsigned NOT NULL COMMENT 'FK to EventQueue',
  `dest_queue_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to EventQueue for destination',
  `time_stamp` datetime NOT NULL COMMENT 'When this forward event occurred.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_event_forward_event_queue_id` (`event_queue_id`),
  KEY `FK_civicrm_mailing_event_forward_dest_queue_id` (`dest_queue_id`),
  CONSTRAINT `FK_civicrm_mailing_event_forward_dest_queue_id` FOREIGN KEY (`dest_queue_id`) REFERENCES `civicrm_mailing_event_queue` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_mailing_event_forward_event_queue_id` FOREIGN KEY (`event_queue_id`) REFERENCES `civicrm_mailing_event_queue` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_event_opened
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_event_opened` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_queue_id` int(10) unsigned NOT NULL COMMENT 'FK to EventQueue',
  `time_stamp` datetime NOT NULL COMMENT 'When this open event occurred.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_event_opened_event_queue_id` (`event_queue_id`),
  CONSTRAINT `FK_civicrm_mailing_event_opened_event_queue_id` FOREIGN KEY (`event_queue_id`) REFERENCES `civicrm_mailing_event_queue` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_event_queue
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_event_queue` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `job_id` int(10) unsigned NOT NULL COMMENT 'FK to Job',
  `email_id` int(10) unsigned DEFAULT NULL,
  `contact_id` int(10) unsigned NOT NULL COMMENT 'FK to Contact',
  `hash` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Security hash',
  `phone_id` int(10) unsigned DEFAULT NULL,
  `activity_id` int(10) unsigned DEFAULT NULL COMMENT 'Activity id of activity type email and bulk mail.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_event_queue_job_id` (`job_id`),
  KEY `FK_civicrm_mailing_event_queue_email_id` (`email_id`),
  KEY `FK_civicrm_mailing_event_queue_contact_id` (`contact_id`),
  KEY `FK_civicrm_mailing_event_queue_phone_id` (`phone_id`),
  KEY `FK_civicrm_mailing_event_queue_activity_id` (`activity_id`),
  CONSTRAINT `FK_civicrm_mailing_event_queue_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `civicrm_activity` (`id`),
  CONSTRAINT `FK_civicrm_mailing_event_queue_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_mailing_event_queue_email_id` FOREIGN KEY (`email_id`) REFERENCES `civicrm_email` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_mailing_event_queue_job_id` FOREIGN KEY (`job_id`) REFERENCES `civicrm_mailing_job` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_mailing_event_queue_phone_id` FOREIGN KEY (`phone_id`) REFERENCES `civicrm_phone` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_event_reply
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_event_reply` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_queue_id` int(10) unsigned NOT NULL COMMENT 'FK to EventQueue',
  `time_stamp` datetime NOT NULL COMMENT 'When this reply event occurred.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_event_reply_event_queue_id` (`event_queue_id`),
  CONSTRAINT `FK_civicrm_mailing_event_reply_event_queue_id` FOREIGN KEY (`event_queue_id`) REFERENCES `civicrm_mailing_event_queue` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_event_subscribe
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_event_subscribe` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` int(10) unsigned NOT NULL COMMENT 'FK to Group',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'FK to Contact',
  `hash` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Security hash',
  `time_stamp` datetime NOT NULL COMMENT 'When this subscription event occurred.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_event_subscribe_group_id` (`group_id`),
  KEY `FK_civicrm_mailing_event_subscribe_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_mailing_event_subscribe_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_mailing_event_subscribe_group_id` FOREIGN KEY (`group_id`) REFERENCES `civicrm_group` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_event_trackable_url_open
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_event_trackable_url_open` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_queue_id` int(10) unsigned NOT NULL COMMENT 'FK to EventQueue',
  `trackable_url_id` int(10) unsigned NOT NULL COMMENT 'FK to TrackableURL',
  `time_stamp` datetime NOT NULL COMMENT 'When this trackable URL open occurred.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_event_trackable_url_open_event_queue_id` (`event_queue_id`),
  KEY `FK_civicrm_mailing_event_trackable_url_open_trackable_url_id` (`trackable_url_id`),
  CONSTRAINT `FK_civicrm_mailing_event_trackable_url_open_event_queue_id` FOREIGN KEY (`event_queue_id`) REFERENCES `civicrm_mailing_event_queue` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_mailing_event_trackable_url_open_trackable_url_id` FOREIGN KEY (`trackable_url_id`) REFERENCES `civicrm_mailing_trackable_url` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_event_unsubscribe
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_event_unsubscribe` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_queue_id` int(10) unsigned NOT NULL COMMENT 'FK to EventQueue',
  `org_unsubscribe` tinyint(4) NOT NULL COMMENT 'Unsubscribe at org- or group-level',
  `time_stamp` datetime NOT NULL COMMENT 'When this delivery event occurred.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_event_unsubscribe_event_queue_id` (`event_queue_id`),
  CONSTRAINT `FK_civicrm_mailing_event_unsubscribe_event_queue_id` FOREIGN KEY (`event_queue_id`) REFERENCES `civicrm_mailing_event_queue` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_group
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `mailing_id` int(10) unsigned NOT NULL COMMENT 'The ID of a previous mailing to include/exclude recipients.',
  `group_type` enum('Include','Exclude','Base') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Are the members of the group included or excluded?.',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of table where item being referenced is stored.',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to the referenced item.',
  `search_id` int(11) DEFAULT NULL COMMENT 'The filtering search. custom search id or -1 for civicrm api search',
  `search_args` text COLLATE utf8_unicode_ci COMMENT 'The arguments to be sent to the search function',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_group_mailing_id` (`mailing_id`),
  CONSTRAINT `FK_civicrm_mailing_group_mailing_id` FOREIGN KEY (`mailing_id`) REFERENCES `civicrm_mailing` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_job
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_job` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `mailing_id` int(10) unsigned NOT NULL COMMENT 'The ID of the mailing this Job will send.',
  `scheduled_date` datetime DEFAULT NULL COMMENT 'date on which this job was scheduled.',
  `start_date` datetime DEFAULT NULL COMMENT 'date on which this job was started.',
  `end_date` datetime DEFAULT NULL COMMENT 'date on which this job ended.',
  `status` enum('Scheduled','Running','Complete','Paused','Canceled') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'The state of this job',
  `is_test` tinyint(4) DEFAULT '0' COMMENT 'Is this job for a test mail?',
  `job_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Type of mailling job: null | child ',
  `parent_id` int(10) unsigned DEFAULT NULL COMMENT 'Parent job id',
  `job_offset` int(11) DEFAULT '0' COMMENT 'Offset of the child job',
  `job_limit` int(11) DEFAULT '0' COMMENT 'Queue size limit for each child job',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_job_mailing_id` (`mailing_id`),
  KEY `FK_civicrm_mailing_job_parent_id` (`parent_id`),
  CONSTRAINT `FK_civicrm_mailing_job_mailing_id` FOREIGN KEY (`mailing_id`) REFERENCES `civicrm_mailing` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_mailing_job_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `civicrm_mailing_job` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_recipients
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_recipients` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `mailing_id` int(10) unsigned NOT NULL COMMENT 'The ID of the mailing this Job will send.',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'FK to Contact',
  `email_id` int(10) unsigned DEFAULT NULL,
  `phone_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_recipients_mailing_id` (`mailing_id`),
  KEY `FK_civicrm_mailing_recipients_contact_id` (`contact_id`),
  KEY `FK_civicrm_mailing_recipients_email_id` (`email_id`),
  KEY `FK_civicrm_mailing_recipients_phone_id` (`phone_id`),
  CONSTRAINT `FK_civicrm_mailing_recipients_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_mailing_recipients_email_id` FOREIGN KEY (`email_id`) REFERENCES `civicrm_email` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_mailing_recipients_mailing_id` FOREIGN KEY (`mailing_id`) REFERENCES `civicrm_mailing` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_mailing_recipients_phone_id` FOREIGN KEY (`phone_id`) REFERENCES `civicrm_phone` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_spool
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_spool` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `job_id` int(10) unsigned NOT NULL COMMENT 'The ID of the Job .',
  `recipient_email` text COLLATE utf8_unicode_ci COMMENT 'The email of the receipients this mail is to be sent.',
  `headers` text COLLATE utf8_unicode_ci COMMENT 'The header information of this mailing .',
  `body` text COLLATE utf8_unicode_ci COMMENT 'The body of this mailing.',
  `added_at` datetime DEFAULT NULL COMMENT 'date on which this job was added.',
  `removed_at` datetime DEFAULT NULL COMMENT 'date on which this job was removed.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_spool_job_id` (`job_id`),
  CONSTRAINT `FK_civicrm_mailing_spool_job_id` FOREIGN KEY (`job_id`) REFERENCES `civicrm_mailing_job` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mailing_trackable_url
# ------------------------------------------------------------

CREATE TABLE `civicrm_mailing_trackable_url` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `url` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'The URL to be tracked.',
  `mailing_id` int(10) unsigned NOT NULL COMMENT 'FK to the mailing',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mailing_trackable_url_mailing_id` (`mailing_id`),
  CONSTRAINT `FK_civicrm_mailing_trackable_url_mailing_id` FOREIGN KEY (`mailing_id`) REFERENCES `civicrm_mailing` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_managed
# ------------------------------------------------------------

CREATE TABLE `civicrm_managed` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Surrogate Key',
  `module` varchar(127) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of the module which declared this object',
  `name` varchar(127) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Symbolic name used by the module to identify the object',
  `entity_type` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'API entity type',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to the referenced item.',
  PRIMARY KEY (`id`),
  KEY `UI_managed_module_name` (`module`,`name`),
  KEY `UI_managed_entity` (`entity_type`,`entity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mapping
# ------------------------------------------------------------

CREATE TABLE `civicrm_mapping` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Mapping ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of Mapping',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Description of Mapping.',
  `mapping_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Mapping Type',
  PRIMARY KEY (`id`),
  KEY `UI_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_mapping_field
# ------------------------------------------------------------

CREATE TABLE `civicrm_mapping_field` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Mapping Field ID',
  `mapping_id` int(10) unsigned NOT NULL COMMENT 'Mapping to which this field belongs',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Mapping field key',
  `contact_type` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Contact Type in mapping',
  `column_number` int(10) unsigned NOT NULL COMMENT 'Column number for mapping set',
  `location_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Location type of this mapping, if required',
  `phone_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Which type of phone does this number belongs.',
  `im_provider_id` int(10) unsigned DEFAULT NULL COMMENT 'Which type of IM Provider does this name belong.',
  `website_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Which type of website does this site belong',
  `relationship_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Relationship type, if required',
  `relationship_direction` varchar(6) COLLATE utf8_unicode_ci DEFAULT NULL,
  `grouping` int(10) unsigned DEFAULT '1' COMMENT 'Used to group mapping_field records into related sets (e.g. for criteria sets in search builder mappings).',
  `operator` enum('=','!=','>','<','>=','<=','IN','NOT IN','LIKE','NOT LIKE','IS NULL','IS NOT NULL') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'SQL WHERE operator for search-builder mapping fields (search criteria).',
  `value` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'SQL WHERE value for search-builder mapping fields.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_mapping_field_mapping_id` (`mapping_id`),
  KEY `FK_civicrm_mapping_field_location_type_id` (`location_type_id`),
  KEY `FK_civicrm_mapping_field_relationship_type_id` (`relationship_type_id`),
  CONSTRAINT `FK_civicrm_mapping_field_location_type_id` FOREIGN KEY (`location_type_id`) REFERENCES `civicrm_location_type` (`id`),
  CONSTRAINT `FK_civicrm_mapping_field_mapping_id` FOREIGN KEY (`mapping_id`) REFERENCES `civicrm_mapping` (`id`),
  CONSTRAINT `FK_civicrm_mapping_field_relationship_type_id` FOREIGN KEY (`relationship_type_id`) REFERENCES `civicrm_relationship_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_membership
# ------------------------------------------------------------

CREATE TABLE `civicrm_membership` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Membership Id',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'FK to Contact ID',
  `membership_type_id` int(10) unsigned NOT NULL COMMENT 'FK to Membership Type',
  `join_date` date DEFAULT NULL COMMENT 'Beginning of initial membership period (member since...).',
  `start_date` date DEFAULT NULL COMMENT 'Beginning of current uninterrupted membership period.',
  `end_date` date DEFAULT NULL COMMENT 'Current membership period expire date.',
  `source` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status_id` int(10) unsigned NOT NULL COMMENT 'FK to Membership Status',
  `is_override` tinyint(4) DEFAULT NULL COMMENT 'Admin users may set a manual status which overrides the calculated status. When this flag is true, automated status update scripts should NOT modify status for the record.',
  `reminder_date` date DEFAULT NULL COMMENT 'When should a reminder be sent.',
  `owner_membership_id` int(10) unsigned DEFAULT NULL COMMENT 'Optional FK to Parent Membership.',
  `is_test` tinyint(4) DEFAULT '0',
  `is_pay_later` tinyint(4) DEFAULT '0',
  `contribution_recur_id` int(10) unsigned DEFAULT NULL COMMENT 'Conditional foreign key to civicrm_contribution_recur id. Each membership in connection with a recurring contribution carries a foreign key to the recurring contribution record. This assumes we can track these processor initiated events.',
  `campaign_id` int(10) unsigned DEFAULT NULL COMMENT 'The campaign for which this membership is attached.',
  PRIMARY KEY (`id`),
  KEY `index_owner_membership_id` (`owner_membership_id`),
  KEY `FK_civicrm_membership_contact_id` (`contact_id`),
  KEY `FK_civicrm_membership_membership_type_id` (`membership_type_id`),
  KEY `FK_civicrm_membership_status_id` (`status_id`),
  KEY `FK_civicrm_membership_contribution_recur_id` (`contribution_recur_id`),
  KEY `FK_civicrm_membership_campaign_id` (`campaign_id`),
  CONSTRAINT `FK_civicrm_membership_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_membership_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_membership_contribution_recur_id` FOREIGN KEY (`contribution_recur_id`) REFERENCES `civicrm_contribution_recur` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_membership_membership_type_id` FOREIGN KEY (`membership_type_id`) REFERENCES `civicrm_membership_type` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_membership_owner_membership_id` FOREIGN KEY (`owner_membership_id`) REFERENCES `civicrm_membership` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_membership_status_id` FOREIGN KEY (`status_id`) REFERENCES `civicrm_membership_status` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_membership_block
# ------------------------------------------------------------

CREATE TABLE `civicrm_membership_block` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Membership Id',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name for Membership Status',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_contribution_page.id',
  `membership_types` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Membership types to be exposed by this block',
  `membership_type_default` int(10) unsigned DEFAULT NULL COMMENT 'Optional foreign key to membership_type',
  `display_min_fee` tinyint(4) DEFAULT '1' COMMENT 'Display minimum membership fee',
  `is_separate_payment` tinyint(4) DEFAULT '1' COMMENT 'Should membership transactions be processed separately',
  `new_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title to display at top of block',
  `new_text` text COLLATE utf8_unicode_ci COMMENT 'Text to display below title',
  `renewal_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title for renewal',
  `renewal_text` text COLLATE utf8_unicode_ci COMMENT 'Text to display for member renewal',
  `is_required` tinyint(4) DEFAULT '0' COMMENT 'Is membership sign up optional',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this membership_block enabled',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_membership_block_entity_id` (`entity_id`),
  KEY `FK_civicrm_membership_block_membership_type_default` (`membership_type_default`),
  CONSTRAINT `FK_civicrm_membership_block_entity_id` FOREIGN KEY (`entity_id`) REFERENCES `civicrm_contribution_page` (`id`),
  CONSTRAINT `FK_civicrm_membership_block_membership_type_default` FOREIGN KEY (`membership_type_default`) REFERENCES `civicrm_membership_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_membership_log
# ------------------------------------------------------------

CREATE TABLE `civicrm_membership_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `membership_id` int(10) unsigned NOT NULL COMMENT 'FK to Membership table',
  `status_id` int(10) unsigned NOT NULL COMMENT 'New status assigned to membership by this action. FK to Membership Status',
  `start_date` date DEFAULT NULL COMMENT 'New membership period start date',
  `end_date` date DEFAULT NULL COMMENT 'New membership period expiration date.',
  `modified_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID of person under whose credentials this data modification was made.',
  `modified_date` date DEFAULT NULL COMMENT 'Date this membership modification action was logged.',
  `renewal_reminder_date` date DEFAULT NULL COMMENT 'The day we sent a renewal reminder',
  `membership_type_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Membership Type.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_membership_log_membership_id` (`membership_id`),
  KEY `FK_civicrm_membership_log_status_id` (`status_id`),
  KEY `FK_civicrm_membership_log_modified_id` (`modified_id`),
  KEY `FK_civicrm_membership_log_membership_type_id` (`membership_type_id`),
  CONSTRAINT `FK_civicrm_membership_log_membership_id` FOREIGN KEY (`membership_id`) REFERENCES `civicrm_membership` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_membership_log_membership_type_id` FOREIGN KEY (`membership_type_id`) REFERENCES `civicrm_membership_type` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_membership_log_modified_id` FOREIGN KEY (`modified_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_membership_log_status_id` FOREIGN KEY (`status_id`) REFERENCES `civicrm_membership_status` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_membership_payment
# ------------------------------------------------------------

CREATE TABLE `civicrm_membership_payment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `membership_id` int(10) unsigned NOT NULL COMMENT 'FK to Membership table',
  `contribution_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to contribution table.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_contribution_membership` (`contribution_id`,`membership_id`),
  KEY `FK_civicrm_membership_payment_membership_id` (`membership_id`),
  CONSTRAINT `FK_civicrm_membership_payment_contribution_id` FOREIGN KEY (`contribution_id`) REFERENCES `civicrm_contribution` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_membership_payment_membership_id` FOREIGN KEY (`membership_id`) REFERENCES `civicrm_membership` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_membership_status
# ------------------------------------------------------------

CREATE TABLE `civicrm_membership_status` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Membership Id',
  `name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name for Membership Status',
  `label` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Label for Membership Status',
  `start_event` enum('start_date','end_date','join_date') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Event when this status starts.',
  `start_event_adjust_unit` enum('day','month','year') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Unit used for adjusting from start_event.',
  `start_event_adjust_interval` int(11) DEFAULT NULL COMMENT 'Status range begins this many units from start_event.',
  `end_event` enum('start_date','end_date','join_date') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Event after which this status ends.',
  `end_event_adjust_unit` enum('day','month','year') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Unit used for adjusting from the ending event.',
  `end_event_adjust_interval` int(11) DEFAULT NULL COMMENT 'Status range ends this many units from end_event.',
  `is_current_member` tinyint(4) DEFAULT NULL COMMENT 'Does this status aggregate to current members (e.g. New, Renewed, Grace might all be TRUE... while Unrenewed, Lapsed, Inactive would be FALSE).',
  `is_admin` tinyint(4) DEFAULT NULL COMMENT 'Is this status for admin/manual assignment only.',
  `weight` int(11) DEFAULT NULL,
  `is_default` tinyint(4) DEFAULT NULL COMMENT 'Assign this status to a membership record if no other status match is found.',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this membership_status enabled.',
  `is_reserved` tinyint(4) DEFAULT '0' COMMENT 'Is this membership_status reserved.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_membership_type
# ------------------------------------------------------------

CREATE TABLE `civicrm_membership_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Membership Id',
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Which Domain is this match entry for',
  `name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of Membership Type',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Description of Membership Type',
  `member_of_contact_id` int(10) unsigned NOT NULL COMMENT 'Owner organization for this membership type. FK to Contact ID',
  `contribution_type_id` int(10) unsigned NOT NULL COMMENT 'If membership is paid by a contribution - what contribution type should be used. FK to Contribution Type ID',
  `minimum_fee` decimal(20,2) DEFAULT '0.00' COMMENT 'Minimum fee for this membership (0 for free/complimentary memberships).',
  `duration_unit` enum('day','month','year','lifetime') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Unit in which membership period is expressed.',
  `duration_interval` int(11) DEFAULT NULL COMMENT 'Number of duration units in membership period (e.g. 1 year, 12 months).',
  `period_type` enum('rolling','fixed') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Rolling membership period starts on signup date. Fixed membership periods start on fixed_period_start_day.',
  `fixed_period_start_day` int(11) DEFAULT NULL COMMENT 'For fixed period memberships, month and day (mmdd) on which subscription/membership will start. Period start is back-dated unless after rollover day.',
  `fixed_period_rollover_day` int(11) DEFAULT NULL COMMENT 'For fixed period memberships, signups after this day (mmdd) rollover to next period.',
  `relationship_type_id` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'FK to Relationship Type ID',
  `relationship_direction` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `visibility` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `weight` int(11) DEFAULT NULL,
  `renewal_msg_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_msg_template.id',
  `renewal_reminder_day` int(11) DEFAULT NULL COMMENT 'Number of days prior to expiration to send renewal reminder',
  `receipt_text_signup` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Receipt Text for membership signup',
  `receipt_text_renewal` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Receipt Text for membership renewal',
  `autorenewal_msg_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_msg_template.id',
  `auto_renew` tinyint(4) DEFAULT '0' COMMENT '0 = No auto-renew option; 1 = Give option, but not required; 2 = Auto-renew required;',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this membership_type enabled',
  PRIMARY KEY (`id`),
  KEY `index_relationship_type_id` (`relationship_type_id`),
  KEY `FK_civicrm_membership_type_domain_id` (`domain_id`),
  KEY `FK_civicrm_membership_type_member_of_contact_id` (`member_of_contact_id`),
  KEY `FK_civicrm_membership_type_contribution_type_id` (`contribution_type_id`),
  KEY `FK_civicrm_membership_type_renewal_msg_id` (`renewal_msg_id`),
  CONSTRAINT `FK_civicrm_membership_type_contribution_type_id` FOREIGN KEY (`contribution_type_id`) REFERENCES `civicrm_contribution_type` (`id`),
  CONSTRAINT `FK_civicrm_membership_type_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`),
  CONSTRAINT `FK_civicrm_membership_type_member_of_contact_id` FOREIGN KEY (`member_of_contact_id`) REFERENCES `civicrm_contact` (`id`),
  CONSTRAINT `FK_civicrm_membership_type_renewal_msg_id` FOREIGN KEY (`renewal_msg_id`) REFERENCES `civicrm_msg_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_menu
# ------------------------------------------------------------

CREATE TABLE `civicrm_menu` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Which Domain is this menu item for',
  `path` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Path Name',
  `path_arguments` text COLLATE utf8_unicode_ci COMMENT 'Arguments to pass to the url',
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Menu Title',
  `access_callback` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Function to call to check access permissions',
  `access_arguments` text COLLATE utf8_unicode_ci COMMENT 'Arguments to pass to access callback',
  `page_callback` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'function to call for this url',
  `page_arguments` text COLLATE utf8_unicode_ci COMMENT 'Arguments to pass to page callback',
  `breadcrumb` text COLLATE utf8_unicode_ci COMMENT 'Breadcrumb for the path.',
  `return_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Url where a page should redirected to, if next url not known.',
  `return_url_args` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Arguments to pass to return_url',
  `component_id` int(10) unsigned DEFAULT NULL COMMENT 'Component that this menu item belongs to',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this menu item active?',
  `is_public` tinyint(4) DEFAULT NULL COMMENT 'Is this menu accessible to the public?',
  `is_exposed` tinyint(4) DEFAULT NULL COMMENT 'Is this menu exposed to the navigation system?',
  `is_ssl` tinyint(4) DEFAULT NULL COMMENT 'Should this menu be exposed via SSL if enabled?',
  `weight` int(11) NOT NULL DEFAULT '1' COMMENT 'Ordering of the menu items in various blocks.',
  `type` int(11) NOT NULL DEFAULT '1' COMMENT 'Drupal menu type.',
  `page_type` int(11) NOT NULL DEFAULT '1' COMMENT 'CiviCRM menu type.',
  `skipBreadcrumb` tinyint(4) DEFAULT NULL COMMENT 'skip this url being exposed to breadcrumb',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_path_domain_id` (`path`,`domain_id`),
  KEY `FK_civicrm_menu_domain_id` (`domain_id`),
  KEY `FK_civicrm_menu_component_id` (`component_id`),
  CONSTRAINT `FK_civicrm_menu_component_id` FOREIGN KEY (`component_id`) REFERENCES `civicrm_component` (`id`),
  CONSTRAINT `FK_civicrm_menu_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_msg_template
# ------------------------------------------------------------

CREATE TABLE `civicrm_msg_template` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Message Template ID',
  `msg_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Descriptive title of message',
  `msg_subject` text COLLATE utf8_unicode_ci COMMENT 'Subject for email message.',
  `msg_text` text COLLATE utf8_unicode_ci COMMENT 'Text formatted message',
  `msg_html` text COLLATE utf8_unicode_ci COMMENT 'HTML formatted message',
  `is_active` tinyint(4) DEFAULT '1',
  `workflow_id` int(10) unsigned DEFAULT NULL COMMENT 'a pseudo-FK to civicrm_option_value',
  `is_default` tinyint(4) DEFAULT '1' COMMENT 'is this the default message template for the workflow referenced by workflow_id?',
  `is_reserved` tinyint(4) DEFAULT NULL COMMENT 'is this the reserved message template which we ship for the workflow referenced by workflow_id?',
  `pdf_format_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_option_value containing PDF Page Format.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_msg_template_pdf_format_id` (`pdf_format_id`),
  CONSTRAINT `FK_civicrm_msg_template_pdf_format_id` FOREIGN KEY (`pdf_format_id`) REFERENCES `civicrm_option_value` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_navigation
# ------------------------------------------------------------

CREATE TABLE `civicrm_navigation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Which Domain is this navigation item for',
  `label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Navigation Title',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Internal Name',
  `url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'url in case of custom navigation link',
  `permission` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Permission for menu item',
  `permission_operator` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Permission Operator',
  `parent_id` int(10) unsigned DEFAULT NULL COMMENT 'Parent navigation item, used for grouping',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this navigation item active?',
  `has_separator` tinyint(4) DEFAULT NULL COMMENT 'If separator needs to be added after this menu item',
  `weight` int(11) DEFAULT NULL COMMENT 'Ordering of the navigation items in various blocks.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_navigation_domain_id` (`domain_id`),
  KEY `FK_civicrm_navigation_parent_id` (`parent_id`),
  CONSTRAINT `FK_civicrm_navigation_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`),
  CONSTRAINT `FK_civicrm_navigation_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `civicrm_navigation` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_note
# ------------------------------------------------------------

CREATE TABLE `civicrm_note` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Note ID',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of table where item being referenced is stored.',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to the referenced item.',
  `note` text COLLATE utf8_unicode_ci COMMENT 'Note and/or Comment.',
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID creator',
  `modified_date` date DEFAULT NULL COMMENT 'When was this note last modified/edited',
  `subject` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'subject of note description',
  `privacy` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Foreign Key to Note Privacy Level (which is an option value pair and hence an implicit FK)',
  PRIMARY KEY (`id`),
  KEY `index_entity` (`entity_table`,`entity_id`),
  KEY `FK_civicrm_note_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_note_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_openid
# ------------------------------------------------------------

CREATE TABLE `civicrm_openid` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique OpenID ID',
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `location_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Which Location does this email belong to.',
  `openid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'the OpenID (or OpenID-style http://username.domain/) unique identifier for this contact mainly used for logging in to CiviCRM',
  `allowed_to_login` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Whether or not this user is allowed to login',
  `is_primary` tinyint(4) DEFAULT '0' COMMENT 'Is this the primary email for this contact and location.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_openid` (`openid`),
  KEY `index_location_type` (`location_type_id`),
  KEY `FK_civicrm_openid_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_openid_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_option_group
# ------------------------------------------------------------

CREATE TABLE `civicrm_option_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Option Group ID',
  `name` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Option group name. Used as selection key by class properties which lookup options in civicrm_option_value.',
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Option group description.',
  `is_reserved` tinyint(4) DEFAULT '1',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this option group active?',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_option_value
# ------------------------------------------------------------

CREATE TABLE `civicrm_option_value` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Option ID',
  `option_group_id` int(10) unsigned NOT NULL COMMENT 'Group which this option belongs to.',
  `label` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Option string as displayed to users - e.g. the label in an HTML OPTION tag.',
  `value` varchar(512) COLLATE utf8_unicode_ci NOT NULL COMMENT 'The actual value stored (as a foreign key) in the data record. Functions which need lookup option_value.title should use civicrm_option_value.option_group_id plus civicrm_option_value.value as the key.',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Stores a fixed (non-translated) name for this option value. Lookup functions should use the name as the key for the option value row.',
  `grouping` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Use to sort and/or set display properties for sub-set(s) of options within an option group. EXAMPLE: Use for college_interest field, to differentiate partners from non-partners.',
  `filter` int(10) unsigned DEFAULT NULL COMMENT 'Bitwise logic can be used to create subsets of options within an option_group for different uses.',
  `is_default` tinyint(4) DEFAULT '0' COMMENT 'Is this the default option for the group?',
  `weight` int(10) unsigned NOT NULL COMMENT 'Controls display sort order.',
  `description` text COLLATE utf8_unicode_ci COMMENT 'Optional description.',
  `is_optgroup` tinyint(4) DEFAULT '0' COMMENT 'Is this row simply a display header? Expected usage is to render these as OPTGROUP tags within a SELECT field list of options?',
  `is_reserved` tinyint(4) DEFAULT '0' COMMENT 'Is this a predefined system object?',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this option active?',
  `component_id` int(10) unsigned DEFAULT NULL COMMENT 'Component that this option value belongs/caters to.',
  `domain_id` int(10) unsigned DEFAULT NULL COMMENT 'Which Domain is this option value for',
  `visibility_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_option_group_id_value` (`value`(255),`option_group_id`),
  KEY `index_option_group_id_name` (`option_group_id`,`name`),
  KEY `FK_civicrm_option_value_component_id` (`component_id`),
  KEY `FK_civicrm_option_value_domain_id` (`domain_id`),
  CONSTRAINT `FK_civicrm_option_value_component_id` FOREIGN KEY (`component_id`) REFERENCES `civicrm_component` (`id`),
  CONSTRAINT `FK_civicrm_option_value_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`),
  CONSTRAINT `FK_civicrm_option_value_option_group_id` FOREIGN KEY (`option_group_id`) REFERENCES `civicrm_option_group` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_participant
# ------------------------------------------------------------

CREATE TABLE `civicrm_participant` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Participant Id',
  `contact_id` int(10) unsigned DEFAULT '0' COMMENT 'FK to Contact ID',
  `event_id` int(10) unsigned DEFAULT '0' COMMENT 'FK to Event ID',
  `status_id` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'Participant status ID. FK to civicrm_participant_status_type. Default of 1 should map to status = Registered.',
  `role_id` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Participant role ID. Implicit FK to civicrm_option_value where option_group = participant_role.',
  `register_date` datetime DEFAULT NULL COMMENT 'When did contact register for event?',
  `source` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Source of this event registration.',
  `fee_level` text COLLATE utf8_unicode_ci COMMENT 'Populate with the label (text) associated with a fee level for paid events with multiple levels. Note that we store the label value and not the key',
  `is_test` tinyint(4) DEFAULT '0',
  `is_pay_later` tinyint(4) DEFAULT '0',
  `fee_amount` decimal(20,2) DEFAULT NULL COMMENT 'actual processor fee if known - may be 0.',
  `registered_by_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Participant ID',
  `discount_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Discount ID',
  `fee_currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value derived from config setting.',
  `campaign_id` int(10) unsigned DEFAULT NULL COMMENT 'The campaign for which this participant has been registered.',
  `discount_amount` int(10) unsigned DEFAULT '0' COMMENT 'Discount Amount',
  `cart_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_event_carts',
  `must_wait` tinyint(4) DEFAULT '0' COMMENT 'On Waiting List',
  PRIMARY KEY (`id`),
  KEY `index_status_id` (`status_id`),
  KEY `index_role_id` (`role_id`),
  KEY `FK_civicrm_participant_contact_id` (`contact_id`),
  KEY `FK_civicrm_participant_event_id` (`event_id`),
  KEY `FK_civicrm_participant_registered_by_id` (`registered_by_id`),
  KEY `FK_civicrm_participant_discount_id` (`discount_id`),
  KEY `FK_civicrm_participant_campaign_id` (`campaign_id`),
  KEY `FK_civicrm_participant_cart_id` (`cart_id`),
  CONSTRAINT `FK_civicrm_participant_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_participant_cart_id` FOREIGN KEY (`cart_id`) REFERENCES `civicrm_event_carts` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_participant_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_participant_discount_id` FOREIGN KEY (`discount_id`) REFERENCES `civicrm_discount` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_participant_event_id` FOREIGN KEY (`event_id`) REFERENCES `civicrm_event` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_participant_registered_by_id` FOREIGN KEY (`registered_by_id`) REFERENCES `civicrm_participant` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_participant_status_id` FOREIGN KEY (`status_id`) REFERENCES `civicrm_participant_status_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_participant_payment
# ------------------------------------------------------------

CREATE TABLE `civicrm_participant_payment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Participant Payment Id',
  `participant_id` int(10) unsigned NOT NULL COMMENT 'Participant Id (FK)',
  `contribution_id` int(10) unsigned NOT NULL COMMENT 'FK to contribution table.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_contribution_participant` (`contribution_id`,`participant_id`),
  KEY `FK_civicrm_participant_payment_participant_id` (`participant_id`),
  CONSTRAINT `FK_civicrm_participant_payment_contribution_id` FOREIGN KEY (`contribution_id`) REFERENCES `civicrm_contribution` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_participant_payment_participant_id` FOREIGN KEY (`participant_id`) REFERENCES `civicrm_participant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_participant_status_type
# ------------------------------------------------------------

CREATE TABLE `civicrm_participant_status_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'unique participant status type id',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'non-localized name of the status type',
  `label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'localized label for display of this status type',
  `class` enum('Positive','Pending','Waiting','Negative') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'the general group of status type this one belongs to',
  `is_reserved` tinyint(4) DEFAULT NULL COMMENT 'whether this is a status type required by the system',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'whether this status type is active',
  `is_counted` tinyint(4) DEFAULT NULL COMMENT 'whether this status type is counted against event size limit',
  `weight` int(10) unsigned NOT NULL COMMENT 'controls sort order',
  `visibility_id` int(10) unsigned DEFAULT NULL COMMENT 'whether the status type is visible to the public, an implicit foreign key to option_value.value related to the `visibility` option_group',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_payment_processor
# ------------------------------------------------------------

CREATE TABLE `civicrm_payment_processor` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Payment Processor ID',
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Which Domain is this match entry for',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Payment Processor Name.',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Payment Processor Description.',
  `payment_processor_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Payment Processor Type.',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this processor active?',
  `is_default` tinyint(4) DEFAULT NULL COMMENT 'Is this processor the default?',
  `is_test` tinyint(4) DEFAULT NULL COMMENT 'Is this processor for a test site?',
  `user_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `signature` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_site` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_api` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_recur` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_button` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `subject` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `class_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `billing_mode` int(10) unsigned NOT NULL COMMENT 'Billing Mode',
  `is_recur` tinyint(4) DEFAULT NULL COMMENT 'Can process recurring contributions',
  `payment_type` int(10) unsigned DEFAULT '1' COMMENT 'Payment Type: Credit or Debit',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name_test_domain_id` (`name`,`is_test`,`domain_id`),
  KEY `FK_civicrm_payment_processor_domain_id` (`domain_id`),
  CONSTRAINT `FK_civicrm_payment_processor_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_payment_processor_type
# ------------------------------------------------------------

CREATE TABLE `civicrm_payment_processor_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Payment Processor Type ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Payment Processor Name.',
  `title` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Payment Processor Name.',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Payment Processor Description.',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this processor active?',
  `is_default` tinyint(4) DEFAULT NULL COMMENT 'Is this processor the default?',
  `user_name_label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password_label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `signature_label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `subject_label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `class_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_site_default` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_api_default` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_recur_default` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_button_default` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_site_test_default` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_api_test_default` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_recur_test_default` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url_button_test_default` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `billing_mode` int(10) unsigned NOT NULL COMMENT 'Billing Mode',
  `is_recur` tinyint(4) DEFAULT NULL COMMENT 'Can process recurring contributions',
  `payment_type` int(10) unsigned DEFAULT '1' COMMENT 'Payment Type: Credit or Debit',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_pcp
# ------------------------------------------------------------

CREATE TABLE `civicrm_pcp` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Personal Campaign Page ID',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'FK to Contact ID',
  `status_id` int(10) unsigned NOT NULL,
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `intro_text` text COLLATE utf8_unicode_ci,
  `page_text` text COLLATE utf8_unicode_ci,
  `donate_link_text` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `page_id` int(10) unsigned NOT NULL COMMENT 'The Page which triggered this pcp',
  `page_type` varchar(64) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'contribute',
  `pcp_block_id` int(10) unsigned NOT NULL COMMENT 'The pcp block that this pcp page was created from',
  `is_thermometer` int(10) unsigned DEFAULT '0',
  `is_honor_roll` int(10) unsigned DEFAULT '0',
  `goal_amount` decimal(20,2) DEFAULT NULL COMMENT 'Goal amount of this Personal Campaign Page.',
  `currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  `is_active` tinyint(4) DEFAULT '0' COMMENT 'Is Personal Campaign Page enabled/active?',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_pcp_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_pcp_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_pcp_block
# ------------------------------------------------------------

CREATE TABLE `civicrm_pcp_block` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'PCP block Id',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `entity_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_contribution_page.id',
  `target_entity_type` varchar(64) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'contribute',
  `target_entity_id` int(10) unsigned NOT NULL COMMENT 'The entity that this pcp targets',
  `supporter_profile_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_uf_group.id. Does Personal Campaign Page require manual activation by administrator? (is inactive by default after setup)?',
  `is_approval_needed` tinyint(4) DEFAULT NULL COMMENT 'Does Personal Campaign Page require manual activation by administrator? (is inactive by default after setup)?',
  `is_tellfriend_enabled` tinyint(4) DEFAULT NULL COMMENT 'Does Personal Campaign Page allow using tell a friend?',
  `tellfriend_limit` int(10) unsigned DEFAULT NULL COMMENT 'Maximum recipient fields allowed in tell a friend',
  `link_text` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Link text for PCP.',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is Personal Campaign Page Block enabled/active?',
  `notify_email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'If set, notification is automatically emailed to this email-address on create/update Personal Campaign Page',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_pcp_block_supporter_profile_id` (`supporter_profile_id`),
  CONSTRAINT `FK_civicrm_pcp_block_supporter_profile_id` FOREIGN KEY (`supporter_profile_id`) REFERENCES `civicrm_uf_group` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_persistent
# ------------------------------------------------------------

CREATE TABLE `civicrm_persistent` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Persistent Record Id',
  `context` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Context for which name data pair is to be stored',
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of Context',
  `data` longtext COLLATE utf8_unicode_ci COMMENT 'data associated with name',
  `is_config` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Config Settings',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_phone
# ------------------------------------------------------------

CREATE TABLE `civicrm_phone` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique Phone ID',
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `location_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Which Location does this phone belong to.',
  `is_primary` tinyint(4) DEFAULT '0' COMMENT 'Is this the primary phone for this contact and location.',
  `is_billing` tinyint(4) DEFAULT '0' COMMENT 'Is this the billing?',
  `mobile_provider_id` int(10) unsigned DEFAULT NULL COMMENT 'Which Mobile Provider does this phone belong to.',
  `phone` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Complete phone number.',
  `phone_ext` varchar(16) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional extension for a phone number.',
  `phone_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Which type of phone does this number belongs.',
  PRIMARY KEY (`id`),
  KEY `index_location_type` (`location_type_id`),
  KEY `index_is_primary` (`is_primary`),
  KEY `index_is_billing` (`is_billing`),
  KEY `UI_mobile_provider_id` (`mobile_provider_id`),
  KEY `FK_civicrm_phone_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_phone_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_pledge
# ------------------------------------------------------------

CREATE TABLE `civicrm_pledge` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Pledge ID',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to civicrm_contact.id .',
  `contribution_type_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contribution Type. This is propagated to contribution record when pledge payments are made.',
  `contribution_page_id` int(10) unsigned DEFAULT NULL COMMENT 'The Contribution Page which triggered this contribution',
  `amount` decimal(20,2) NOT NULL COMMENT 'Total pledged amount.',
  `original_installment_amount` decimal(20,2) NOT NULL COMMENT 'Original amount for each of the installments.',
  `currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  `frequency_unit` enum('day','week','month','year') COLLATE utf8_unicode_ci DEFAULT 'month' COMMENT 'Time units for recurrence of pledge payments.',
  `frequency_interval` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'Number of time units for recurrence of pledge payments.',
  `frequency_day` int(10) unsigned NOT NULL DEFAULT '3' COMMENT 'Day in the period when the pledge payment is due e.g. 1st of month, 15th etc. Use this to set the scheduled dates for pledge payments.',
  `installments` int(10) unsigned DEFAULT '1' COMMENT 'Total number of payments to be made.',
  `start_date` datetime NOT NULL COMMENT 'The date the first scheduled pledge occurs.',
  `create_date` datetime NOT NULL COMMENT 'When this pledge record was created.',
  `acknowledge_date` datetime DEFAULT NULL COMMENT 'When a pledge acknowledgement message was sent to the contributor.',
  `modified_date` datetime DEFAULT NULL COMMENT 'Last updated date for this pledge record.',
  `cancel_date` datetime DEFAULT NULL COMMENT 'Date this pledge was cancelled by contributor.',
  `end_date` datetime DEFAULT NULL COMMENT 'Date this pledge finished successfully (total pledge payments equal to or greater than pledged amount).',
  `honor_contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to contact ID. Used when pledge is made in honor of another contact. This is propagated to contribution records when pledge payments are made.',
  `honor_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Implicit FK to civicrm_option_value.',
  `max_reminders` int(10) unsigned DEFAULT '1' COMMENT 'The maximum number of payment reminders to send for any given payment.',
  `initial_reminder_day` int(10) unsigned DEFAULT '5' COMMENT 'Send initial reminder this many days prior to the payment due date.',
  `additional_reminder_day` int(10) unsigned DEFAULT '5' COMMENT 'Send additional reminder this many days after last one sent, up to maximum number of reminders.',
  `status_id` int(10) unsigned DEFAULT NULL COMMENT 'Implicit foreign key to civicrm_option_values in the contribution_status option group.',
  `is_test` tinyint(4) DEFAULT '0',
  `campaign_id` int(10) unsigned DEFAULT NULL COMMENT 'The campaign for which this pledge has been initiated.',
  PRIMARY KEY (`id`),
  KEY `index_status` (`status_id`),
  KEY `FK_civicrm_pledge_contact_id` (`contact_id`),
  KEY `FK_civicrm_pledge_contribution_type_id` (`contribution_type_id`),
  KEY `FK_civicrm_pledge_contribution_page_id` (`contribution_page_id`),
  KEY `FK_civicrm_pledge_honor_contact_id` (`honor_contact_id`),
  KEY `FK_civicrm_pledge_campaign_id` (`campaign_id`),
  CONSTRAINT `FK_civicrm_pledge_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_pledge_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_pledge_contribution_page_id` FOREIGN KEY (`contribution_page_id`) REFERENCES `civicrm_contribution_page` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_pledge_contribution_type_id` FOREIGN KEY (`contribution_type_id`) REFERENCES `civicrm_contribution_type` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_pledge_honor_contact_id` FOREIGN KEY (`honor_contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_pledge_block
# ------------------------------------------------------------

CREATE TABLE `civicrm_pledge_block` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Pledge ID',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'physical tablename for entity being joined to pledge, e.g. civicrm_contact',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'FK to entity table specified in entity_table column.',
  `pledge_frequency_unit` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Delimited list of supported frequency units',
  `is_pledge_interval` tinyint(4) DEFAULT '0' COMMENT 'Is frequency interval exposed on the contribution form.',
  `max_reminders` int(10) unsigned DEFAULT '1' COMMENT 'The maximum number of payment reminders to send for any given payment.',
  `initial_reminder_day` int(10) unsigned DEFAULT '5' COMMENT 'Send initial reminder this many days prior to the payment due date.',
  `additional_reminder_day` int(10) unsigned DEFAULT '5' COMMENT 'Send additional reminder this many days after last one sent, up to maximum number of reminders.',
  PRIMARY KEY (`id`),
  KEY `index_entity` (`entity_table`,`entity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_pledge_payment
# ------------------------------------------------------------

CREATE TABLE `civicrm_pledge_payment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pledge_id` int(10) unsigned NOT NULL COMMENT 'FK to Pledge table',
  `contribution_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to contribution table.',
  `scheduled_amount` decimal(20,2) NOT NULL COMMENT 'Pledged amount for this payment (the actual contribution amount might be different).',
  `actual_amount` decimal(20,2) DEFAULT NULL COMMENT 'Actual amount that is paid as the Pledged installment amount.',
  `currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  `scheduled_date` datetime NOT NULL COMMENT 'The date the pledge payment is supposed to happen.',
  `reminder_date` datetime DEFAULT NULL COMMENT 'The date that the most recent payment reminder was sent.',
  `reminder_count` int(10) unsigned DEFAULT '0' COMMENT 'The number of payment reminders sent.',
  `status_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_contribution_pledge` (`contribution_id`,`pledge_id`),
  KEY `index_status` (`status_id`),
  KEY `FK_civicrm_pledge_payment_pledge_id` (`pledge_id`),
  CONSTRAINT `FK_civicrm_pledge_payment_contribution_id` FOREIGN KEY (`contribution_id`) REFERENCES `civicrm_contribution` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_pledge_payment_pledge_id` FOREIGN KEY (`pledge_id`) REFERENCES `civicrm_pledge` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_preferences
# ------------------------------------------------------------

CREATE TABLE `civicrm_preferences` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Which Domain is this menu item for',
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `is_domain` tinyint(4) DEFAULT NULL COMMENT 'Is this the record for the domain setting?',
  `contact_view_options` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'What tabs are displayed in the contact summary',
  `contact_edit_options` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'What tabs are displayed in the contact edit',
  `advanced_search_options` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'What tabs are displayed in the advanced search screen',
  `user_dashboard_options` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'What tabs are displayed in the contact edit',
  `address_options` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'What fields are displayed from the address table',
  `address_format` text COLLATE utf8_unicode_ci COMMENT 'Format to display the address',
  `mailing_format` text COLLATE utf8_unicode_ci COMMENT 'Format to display a mailing label',
  `display_name_format` text COLLATE utf8_unicode_ci COMMENT 'Format to display contact display name',
  `sort_name_format` text COLLATE utf8_unicode_ci COMMENT 'Format to display contact sort name',
  `address_standardization_provider` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'object name of provider for address standarization',
  `address_standardization_userid` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'user id for provider login',
  `address_standardization_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'url of address standardization service',
  `editor_id` int(10) unsigned DEFAULT NULL COMMENT 'ID of the editor',
  `mailing_backend` text COLLATE utf8_unicode_ci COMMENT 'Smtp Backend configuration.',
  `navigation` text COLLATE utf8_unicode_ci COMMENT 'Store navigation for the Contact',
  `contact_autocomplete_options` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'What Autocomplete has to return',
  PRIMARY KEY (`id`),
  KEY `index_contact_view_options` (`contact_view_options`),
  KEY `index_contact_edit_options` (`contact_edit_options`),
  KEY `index_advanced_search_options` (`advanced_search_options`),
  KEY `index_user_dashboard_options` (`user_dashboard_options`),
  KEY `index_address_options` (`address_options`),
  KEY `FK_civicrm_preferences_domain_id` (`domain_id`),
  KEY `FK_civicrm_preferences_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_preferences_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_preferences_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_preferences_date
# ------------------------------------------------------------

CREATE TABLE `civicrm_preferences_date` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'The meta name for this date (fixed in code)',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Description of this date type.',
  `start` int(11) NOT NULL COMMENT 'The start offset relative to current year',
  `end` int(11) NOT NULL COMMENT 'The end offset relative to current year, can be negative',
  `date_format` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'The date type',
  `time_format` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'time format',
  PRIMARY KEY (`id`),
  KEY `index_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_premiums
# ------------------------------------------------------------

CREATE TABLE `civicrm_premiums` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Joins these premium settings to another object. Always civicrm_contribution_page for now.',
  `entity_id` int(10) unsigned NOT NULL,
  `premiums_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Is the Premiums feature enabled for this page?',
  `premiums_intro_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title for Premiums section.',
  `premiums_intro_text` text COLLATE utf8_unicode_ci COMMENT 'Displayed in <div> at top of Premiums section of page. Text and HTML allowed.',
  `premiums_contact_email` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'This email address is included in receipts if it is populated and a premium has been selected.',
  `premiums_contact_phone` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'This phone number is included in receipts if it is populated and a premium has been selected.',
  `premiums_display_min_contribution` tinyint(4) NOT NULL COMMENT 'Boolean. Should we automatically display minimum contribution amount text after the premium descriptions.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_premiums_product
# ------------------------------------------------------------

CREATE TABLE `civicrm_premiums_product` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Contribution ID',
  `premiums_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to premiums settings record.',
  `product_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to each product object.',
  `weight` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_premiums_product_premiums_id` (`premiums_id`),
  KEY `FK_civicrm_premiums_product_product_id` (`product_id`),
  CONSTRAINT `FK_civicrm_premiums_product_premiums_id` FOREIGN KEY (`premiums_id`) REFERENCES `civicrm_premiums` (`id`),
  CONSTRAINT `FK_civicrm_premiums_product_product_id` FOREIGN KEY (`product_id`) REFERENCES `civicrm_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_prevnext_cache
# ------------------------------------------------------------

CREATE TABLE `civicrm_prevnext_cache` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `entity_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'physical tablename for entity being joined to discount, e.g. civicrm_event',
  `entity_id1` int(10) unsigned NOT NULL COMMENT 'FK to entity table specified in entity_table column.',
  `entity_id2` int(10) unsigned NOT NULL COMMENT 'FK to entity table specified in entity_table column.',
  `cacheKey` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Unique path name for cache element of the searched item',
  `data` longtext COLLATE utf8_unicode_ci COMMENT 'cached snapshot of the serialized data',
  `is_selected` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `index_all` (`cacheKey`,`entity_id1`,`entity_id2`,`entity_table`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_price_field
# ------------------------------------------------------------

CREATE TABLE `civicrm_price_field` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Price Field',
  `price_set_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_price_set',
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Variable name/programmatic handle for this field.',
  `label` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Text for form field label (also friendly name for administering this field).',
  `html_type` enum('Text','Select','Radio','CheckBox') COLLATE utf8_unicode_ci NOT NULL,
  `is_enter_qty` tinyint(4) DEFAULT '0' COMMENT 'Enter a quantity for this field?',
  `help_pre` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display before this field.',
  `help_post` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display after this field.',
  `weight` int(11) DEFAULT '1' COMMENT 'Order in which the fields should appear',
  `is_display_amounts` tinyint(4) DEFAULT '1' COMMENT 'Should the price be displayed next to the label for each option?',
  `options_per_line` int(10) unsigned DEFAULT '1' COMMENT 'number of options per line for checkbox and radio',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this price field active',
  `is_required` tinyint(4) DEFAULT '1' COMMENT 'Is this price field required (value must be > 1)',
  `active_on` datetime DEFAULT NULL COMMENT 'If non-zero, do not show this field before the date specified',
  `expire_on` datetime DEFAULT NULL COMMENT 'If non-zero, do not show this field after the date specified',
  `javascript` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional scripting attributes for field',
  `visibility_id` int(10) unsigned DEFAULT '1' COMMENT 'Implicit FK to civicrm_option_group with name = ''visibility''',
  PRIMARY KEY (`id`),
  KEY `index_name` (`name`),
  KEY `FK_civicrm_price_field_price_set_id` (`price_set_id`),
  CONSTRAINT `FK_civicrm_price_field_price_set_id` FOREIGN KEY (`price_set_id`) REFERENCES `civicrm_price_set` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_price_field_value
# ------------------------------------------------------------

CREATE TABLE `civicrm_price_field_value` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Price Field Value',
  `price_field_id` int(10) unsigned NOT NULL COMMENT 'FK to civicrm_price_field',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8_unicode_ci COMMENT '>Price field option description.',
  `amount` varchar(512) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Price field option amount',
  `count` int(10) unsigned DEFAULT NULL COMMENT 'Number of participants per field option',
  `max_value` int(10) unsigned DEFAULT NULL COMMENT 'Max number of participants per field options',
  `weight` int(11) DEFAULT '1' COMMENT 'Order in which the field options should appear',
  `is_default` tinyint(4) DEFAULT '0' COMMENT 'Is this default price field option',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this price field value active',
  `membership_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Conditional foreign key to civicrm_membership_type.id.',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_price_field_value_price_field_id` (`price_field_id`),
  KEY `FK_civicrm_price_field_value_membership_type_id` (`membership_type_id`),
  CONSTRAINT `FK_civicrm_price_field_value_membership_type_id` FOREIGN KEY (`membership_type_id`) REFERENCES `civicrm_membership_type` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_price_field_value_price_field_id` FOREIGN KEY (`price_field_id`) REFERENCES `civicrm_price_field` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_price_set
# ------------------------------------------------------------

CREATE TABLE `civicrm_price_set` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Price Set',
  `domain_id` int(10) unsigned DEFAULT NULL COMMENT 'Which Domain is this price-set for',
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Variable name/programmatic handle for this set of price fields.',
  `title` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Displayed title for the Price Set.',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this price set active',
  `help_pre` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display before fields in form.',
  `help_post` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display after fields in form.',
  `javascript` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional Javascript script function(s) included on the form with this price_set. Can be used for conditional',
  `extends` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'What components are using this price set?',
  `contribution_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Conditional foreign key to civicrm_contribution_type.id.',
  `is_quick_config` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Is set if edited on Contribution or Event Page rather than through Manage Price Sets',
  `is_reserved` tinyint(4) DEFAULT '0' COMMENT 'Is this a predefined system price set  (i.e. it can not be deleted, edited)?',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name` (`name`),
  KEY `FK_civicrm_price_set_domain_id` (`domain_id`),
  KEY `FK_civicrm_price_set_contribution_type_id` (`contribution_type_id`),
  CONSTRAINT `FK_civicrm_price_set_contribution_type_id` FOREIGN KEY (`contribution_type_id`) REFERENCES `civicrm_contribution_type` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_price_set_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_price_set_entity
# ------------------------------------------------------------

CREATE TABLE `civicrm_price_set_entity` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Price Set Entity',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Table which uses this price set',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Item in table',
  `price_set_id` int(10) unsigned NOT NULL COMMENT 'price set being used',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_entity` (`entity_table`,`entity_id`),
  KEY `FK_civicrm_price_set_entity_price_set_id` (`price_set_id`),
  CONSTRAINT `FK_civicrm_price_set_entity_price_set_id` FOREIGN KEY (`price_set_id`) REFERENCES `civicrm_price_set` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_product
# ------------------------------------------------------------

CREATE TABLE `civicrm_product` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Required product/premium name',
  `description` text COLLATE utf8_unicode_ci COMMENT 'Optional description of the product/premium.',
  `sku` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional product sku or code.',
  `options` text COLLATE utf8_unicode_ci COMMENT 'Store comma-delimited list of color, size, etc. options for the product.',
  `image` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Full or relative URL to uploaded image - fullsize.',
  `thumbnail` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Full or relative URL to image thumbnail.',
  `price` decimal(20,2) DEFAULT NULL COMMENT 'Sell price or market value for premiums. For tax-deductible contributions, this will be stored as non_deductible_amount in the contribution record.',
  `currency` varchar(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '3 character string, value from config setting or input via user.',
  `min_contribution` decimal(20,2) DEFAULT NULL COMMENT 'Minimum contribution required to be eligible to select this premium.',
  `cost` decimal(20,2) DEFAULT NULL COMMENT 'Actual cost of this product. Useful to determine net return from sale or using this as an incentive.',
  `is_active` tinyint(4) NOT NULL COMMENT 'Disabling premium removes it from the premiums_premium join table below.',
  `period_type` enum('rolling','fixed') COLLATE utf8_unicode_ci DEFAULT 'rolling' COMMENT 'Rolling means we set start/end based on current day, fixed means we set start/end for current year or month\n(e.g. 1 year + fixed -> we would set start/end for 1/1/06 thru 12/31/06 for any premium chosen in 2006) ',
  `fixed_period_start_day` int(11) DEFAULT '101' COMMENT 'Month and day (MMDD) that fixed period type subscription or membership starts.',
  `duration_unit` enum('day','month','week','year') COLLATE utf8_unicode_ci DEFAULT 'year',
  `duration_interval` int(11) DEFAULT NULL COMMENT 'Number of units for total duration of subscription, service, membership (e.g. 12 Months).',
  `frequency_unit` enum('day','month','week','year') COLLATE utf8_unicode_ci DEFAULT 'month' COMMENT 'Frequency unit and interval allow option to store actual delivery frequency for a subscription or service.',
  `frequency_interval` int(11) DEFAULT NULL COMMENT 'Number of units for delivery frequency of subscription, service, membership (e.g. every 3 Months).',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_project
# ------------------------------------------------------------

CREATE TABLE `civicrm_project` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Project ID',
  `title` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Project name.',
  `description` text COLLATE utf8_unicode_ci COMMENT 'Optional verbose description of the project. May be used for display - HTML allowed.',
  `logo` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Full or relative URL to optional uploaded logo image for project.',
  `owner_entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of table where project owner being referenced is stored (e.g. civicrm_contact or civicrm_group).',
  `owner_entity_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to project owner (contact, group, etc.).',
  `start_date` datetime DEFAULT NULL COMMENT 'Project start date.',
  `end_date` datetime DEFAULT NULL COMMENT 'Project end date.',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this record active? For Projects: can tasks be created for it, does it appear on project listings, etc.',
  `status_id` int(10) unsigned DEFAULT NULL COMMENT 'Configurable status value (e.g. Planned, Active, Closed...). FK to civicrm_option_value.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_project_owner` (`id`,`owner_entity_table`,`owner_entity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_queue_item
# ------------------------------------------------------------

CREATE TABLE `civicrm_queue_item` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `queue_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of the queue which includes this item',
  `weight` int(11) NOT NULL,
  `submit_time` datetime NOT NULL COMMENT 'date on which this item was submitted to the queue',
  `release_time` datetime DEFAULT NULL COMMENT 'date on which this job becomes available; null if ASAP',
  `data` text COLLATE utf8_unicode_ci COMMENT 'Serialized queue',
  PRIMARY KEY (`id`),
  KEY `index_queueids` (`queue_name`,`weight`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_relationship
# ------------------------------------------------------------

CREATE TABLE `civicrm_relationship` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Relationship ID',
  `contact_id_a` int(10) unsigned NOT NULL COMMENT 'id of the first contact',
  `contact_id_b` int(10) unsigned NOT NULL COMMENT 'id of the second contact',
  `relationship_type_id` int(10) unsigned NOT NULL COMMENT 'id of the relationship',
  `start_date` date DEFAULT NULL COMMENT 'date when the relationship started',
  `end_date` date DEFAULT NULL COMMENT 'date when the relationship ended',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'is the relationship active ?',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional verbose description for the relationship.',
  `is_permission_a_b` tinyint(4) DEFAULT '0' COMMENT 'is contact a has permission to view / edit contact and\n  related data for contact b ?',
  `is_permission_b_a` tinyint(4) DEFAULT '0' COMMENT 'is contact b has permission to view / edit contact and\n  related data for contact a ?',
  `case_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_case',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_relationship_contact_id_a` (`contact_id_a`),
  KEY `FK_civicrm_relationship_contact_id_b` (`contact_id_b`),
  KEY `FK_civicrm_relationship_relationship_type_id` (`relationship_type_id`),
  KEY `FK_civicrm_relationship_case_id` (`case_id`),
  CONSTRAINT `FK_civicrm_relationship_case_id` FOREIGN KEY (`case_id`) REFERENCES `civicrm_case` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_relationship_contact_id_a` FOREIGN KEY (`contact_id_a`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_relationship_contact_id_b` FOREIGN KEY (`contact_id_b`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_relationship_relationship_type_id` FOREIGN KEY (`relationship_type_id`) REFERENCES `civicrm_relationship_type` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_relationship_type
# ------------------------------------------------------------

CREATE TABLE `civicrm_relationship_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `name_a_b` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'name for relationship of contact_a to contact_b.',
  `label_a_b` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'label for relationship of contact_a to contact_b.',
  `name_b_a` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional name for relationship of contact_b to contact_a.',
  `label_b_a` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional label for relationship of contact_b to contact_a.',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional verbose description of the relationship type.',
  `contact_type_a` enum('Individual','Organization','Household') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'If defined, contact_a in a relationship of this type must be a specific contact_type.',
  `contact_type_b` enum('Individual','Organization','Household') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'If defined, contact_b in a relationship of this type must be a specific contact_type.',
  `contact_sub_type_a` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'If defined, contact_sub_type_a in a relationship of this type must be a specific contact_sub_type.',
  `contact_sub_type_b` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'If defined, contact_sub_type_b in a relationship of this type must be a specific contact_sub_type.',
  `is_reserved` tinyint(4) DEFAULT NULL COMMENT 'Is this relationship type a predefined system type (can not be changed or de-activated)?',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this relationship type currently active (i.e. can be used when creating or editing relationships)?',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name_a_b` (`name_a_b`),
  UNIQUE KEY `UI_name_b_a` (`name_b_a`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_report_instance
# ------------------------------------------------------------

CREATE TABLE `civicrm_report_instance` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Report Instance ID',
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Which Domain is this instance for',
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Report Instance Title.',
  `report_id` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'FK to civicrm_option_value for the report template',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'when combined with report_id/template uniquely identifies the instance',
  `args` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'arguments that are passed in the url when invoking the instance',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Report Instance description.',
  `permission` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'permission required to be able to run this instance',
  `grouprole` varchar(1024) COLLATE utf8_unicode_ci DEFAULT NULL,
  `form_values` text COLLATE utf8_unicode_ci COMMENT 'Submitted form values for this report',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this entry active?',
  `email_subject` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Subject of email',
  `email_to` text COLLATE utf8_unicode_ci COMMENT 'comma-separated list of email addresses to send the report to',
  `email_cc` text COLLATE utf8_unicode_ci COMMENT 'comma-separated list of email addresses to send the report to',
  `header` text COLLATE utf8_unicode_ci COMMENT 'comma-separated list of email addresses to send the report to',
  `footer` text COLLATE utf8_unicode_ci COMMENT 'comma-separated list of email addresses to send the report to',
  `navigation_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to navigation ID',
  `is_reserved` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_report_instance_domain_id` (`domain_id`),
  KEY `FK_civicrm_report_instance_navigation_id` (`navigation_id`),
  CONSTRAINT `FK_civicrm_report_instance_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`),
  CONSTRAINT `FK_civicrm_report_instance_navigation_id` FOREIGN KEY (`navigation_id`) REFERENCES `civicrm_navigation` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_saved_search
# ------------------------------------------------------------

CREATE TABLE `civicrm_saved_search` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Saved search ID',
  `form_values` text COLLATE utf8_unicode_ci COMMENT 'Submitted form values for this search',
  `mapping_id` int(10) unsigned DEFAULT NULL COMMENT 'Foreign key to civicrm_mapping used for saved search-builder searches.',
  `search_custom_id` int(10) unsigned DEFAULT NULL COMMENT 'Foreign key to civicrm_option value table used for saved custom searches.',
  `where_clause` text COLLATE utf8_unicode_ci COMMENT 'the sql where clause if a saved search acl',
  `select_tables` text COLLATE utf8_unicode_ci COMMENT 'the tables to be included in a select data',
  `where_tables` text COLLATE utf8_unicode_ci COMMENT 'the tables to be included in the count statement',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_saved_search_mapping_id` (`mapping_id`),
  CONSTRAINT `FK_civicrm_saved_search_mapping_id` FOREIGN KEY (`mapping_id`) REFERENCES `civicrm_mapping` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_setting
# ------------------------------------------------------------

CREATE TABLE `civicrm_setting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `group_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'group name for setting element, useful in caching setting elements',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Unique name for setting',
  `value` text COLLATE utf8_unicode_ci COMMENT 'data associated with this group / name combo',
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Which Domain is this menu item for',
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID if the setting is localized to a contact',
  `is_domain` tinyint(4) DEFAULT NULL COMMENT 'Is this setting a contact specific or site wide setting?',
  `component_id` int(10) unsigned DEFAULT NULL COMMENT 'Component that this menu item belongs to',
  `created_date` datetime DEFAULT NULL COMMENT 'When was the setting created',
  `created_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact, who created this setting',
  PRIMARY KEY (`id`),
  KEY `index_group_name` (`group_name`,`name`),
  KEY `FK_civicrm_setting_domain_id` (`domain_id`),
  KEY `FK_civicrm_setting_contact_id` (`contact_id`),
  KEY `FK_civicrm_setting_component_id` (`component_id`),
  KEY `FK_civicrm_setting_created_id` (`created_id`),
  CONSTRAINT `FK_civicrm_setting_component_id` FOREIGN KEY (`component_id`) REFERENCES `civicrm_component` (`id`),
  CONSTRAINT `FK_civicrm_setting_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_setting_created_id` FOREIGN KEY (`created_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_setting_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_sms_provider
# ------------------------------------------------------------

CREATE TABLE `civicrm_sms_provider` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'SMS Provider ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Provider internal name points to option_value of option_group sms_provider_name',
  `title` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Provider name visible to user',
  `username` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `api_type` int(10) unsigned NOT NULL COMMENT 'points to value in civicrm_option_value for group sms_api_type',
  `api_url` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `api_params` text COLLATE utf8_unicode_ci COMMENT 'the api params in xml, http or smtp format',
  `is_default` tinyint(4) DEFAULT '0',
  `is_active` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_state_province
# ------------------------------------------------------------

CREATE TABLE `civicrm_state_province` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'State / Province ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of State / Province',
  `abbreviation` varchar(4) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '2-4 Character Abbreviation of State / Province',
  `country_id` int(10) unsigned NOT NULL COMMENT 'ID of Country that State / Province belong',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name_country_id` (`name`,`country_id`),
  KEY `FK_civicrm_state_province_country_id` (`country_id`),
  CONSTRAINT `FK_civicrm_state_province_country_id` FOREIGN KEY (`country_id`) REFERENCES `civicrm_country` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_subscription_history
# ------------------------------------------------------------

CREATE TABLE `civicrm_subscription_history` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Internal Id',
  `contact_id` int(10) unsigned NOT NULL COMMENT 'Contact Id',
  `group_id` int(10) unsigned DEFAULT NULL COMMENT 'Group Id',
  `date` datetime NOT NULL COMMENT 'Date of the (un)subscription',
  `method` enum('Admin','Email','Web','API') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'How the (un)subscription was triggered',
  `status` enum('Added','Removed','Pending','Deleted') COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'The state of the contact within the group',
  `tracking` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'IP address or other tracking info',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_subscription_history_contact_id` (`contact_id`),
  KEY `FK_civicrm_subscription_history_group_id` (`group_id`),
  CONSTRAINT `FK_civicrm_subscription_history_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_subscription_history_group_id` FOREIGN KEY (`group_id`) REFERENCES `civicrm_group` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_survey
# ------------------------------------------------------------

CREATE TABLE `civicrm_survey` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Survey id.',
  `title` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Title of the Survey.',
  `campaign_id` int(10) unsigned DEFAULT NULL COMMENT 'Foreign key to the Campaign.',
  `activity_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Implicit FK to civicrm_option_value where option_group = activity_type',
  `recontact_interval` text COLLATE utf8_unicode_ci COMMENT 'Recontact intervals for each status.',
  `instructions` text COLLATE utf8_unicode_ci COMMENT 'Script instructions for volunteers to use for the survey.',
  `release_frequency` int(10) unsigned DEFAULT NULL COMMENT 'Number of days for recurrence of release.',
  `max_number_of_contacts` int(10) unsigned DEFAULT NULL COMMENT 'Maximum number of contacts to allow for survey.',
  `default_number_of_contacts` int(10) unsigned DEFAULT NULL COMMENT 'Default number of contacts to allow for survey.',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this survey enabled or disabled/cancelled?',
  `is_default` tinyint(4) DEFAULT '0' COMMENT 'Is this default survey?',
  `created_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact, who created this Survey.',
  `created_date` datetime DEFAULT NULL COMMENT 'Date and time that Survey was created.',
  `last_modified_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact, who recently edited this Survey.',
  `last_modified_date` datetime DEFAULT NULL COMMENT 'Date and time that Survey was edited last time.',
  `result_id` int(10) unsigned DEFAULT NULL COMMENT 'Used to store option group id.',
  `thankyou_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Title for Thank-you page (header title tag, and display at the top of the page).',
  `thankyou_text` text COLLATE utf8_unicode_ci COMMENT 'text and html allowed. displayed above result on success page',
  `bypass_confirm` tinyint(4) DEFAULT '0' COMMENT 'Used to store option group id.',
  PRIMARY KEY (`id`),
  KEY `UI_activity_type_id` (`activity_type_id`),
  KEY `FK_civicrm_survey_campaign_id` (`campaign_id`),
  KEY `FK_civicrm_survey_created_id` (`created_id`),
  KEY `FK_civicrm_survey_last_modified_id` (`last_modified_id`),
  CONSTRAINT `FK_civicrm_survey_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `civicrm_campaign` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_survey_created_id` FOREIGN KEY (`created_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_survey_last_modified_id` FOREIGN KEY (`last_modified_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_tag
# ------------------------------------------------------------

CREATE TABLE `civicrm_tag` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Tag ID',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of Tag.',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional verbose description of the tag.',
  `parent_id` int(10) unsigned DEFAULT NULL COMMENT 'Optional parent id for this tag.',
  `is_selectable` tinyint(4) DEFAULT '1' COMMENT 'Is this tag selectable / displayed',
  `is_reserved` tinyint(4) DEFAULT '0',
  `is_tagset` tinyint(4) DEFAULT '0',
  `used_for` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact, who created this tag',
  `created_date` datetime DEFAULT NULL COMMENT 'Date and time that tag was created.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_name` (`name`),
  KEY `FK_civicrm_tag_parent_id` (`parent_id`),
  KEY `FK_civicrm_tag_created_id` (`created_id`),
  CONSTRAINT `FK_civicrm_tag_created_id` FOREIGN KEY (`created_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_tag_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `civicrm_tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_task
# ------------------------------------------------------------

CREATE TABLE `civicrm_task` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Task ID',
  `title` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Task name.',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional verbose description of the Task. May be used for display - HTML allowed.',
  `task_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Configurable task type values (e.g. App Submit, App Review...). FK to civicrm_option_value.',
  `owner_entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of table where Task owner being referenced is stored (e.g. civicrm_contact or civicrm_group).',
  `owner_entity_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to Task owner (contact, group, etc.).',
  `parent_entity_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of table where optional Task parent is stored (e.g. civicrm_project, or civicrm_task for sub-tasks).',
  `parent_entity_id` int(10) unsigned DEFAULT NULL COMMENT 'Optional foreign key to Task Parent (project, another task, etc.).',
  `due_date` datetime DEFAULT NULL COMMENT 'Task due date.',
  `priority_id` int(10) unsigned DEFAULT NULL COMMENT 'Configurable priority value (e.g. Critical, High, Medium...). FK to civicrm_option_value.',
  `task_class` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Optional key to a process class related to this task (e.g. CRM_Quest_PreApp).',
  `is_active` tinyint(4) DEFAULT NULL COMMENT 'Is this record active? For tasks: can it be assigned, does it appear on open task listings, etc.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_task_owner` (`id`,`owner_entity_table`,`owner_entity_id`),
  UNIQUE KEY `UI_task_parent` (`id`,`parent_entity_table`,`parent_entity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_task_action_temp_a3916b25c2ffe6ddf98f31dfff0d5dff_4949
# ------------------------------------------------------------

CREATE TABLE `civicrm_task_action_temp_a3916b25c2ffe6ddf98f31dfff0d5dff_4949` (
  `contact_id` int(11) NOT NULL,
  PRIMARY KEY (`contact_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_task_status
# ------------------------------------------------------------

CREATE TABLE `civicrm_task_status` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Task ID',
  `task_id` int(10) unsigned NOT NULL COMMENT 'Status is for which task.',
  `responsible_entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Entity responsible for this task_status instance (table where entity is stored e.g. civicrm_contact or civicrm_group).',
  `responsible_entity_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to responsible entity (contact, group, etc.).',
  `target_entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Optional target entity for this task_status instance, i.e. review this membership application-prospect member contact record is target (table where entity is stored e.g. civicrm_contact or civicrm_group).',
  `target_entity_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to target entity (contact, group, etc.).',
  `status_detail` text COLLATE utf8_unicode_ci COMMENT 'Encoded array of status details used for programmatic progress reporting and tracking.',
  `status_id` int(10) unsigned DEFAULT NULL COMMENT 'Configurable status value (e.g. Not Started, In Progress, Completed, Deferred...). FK to civicrm_option_value.',
  `create_date` datetime DEFAULT NULL COMMENT 'Date this record was created (date work on task started).',
  `modified_date` datetime DEFAULT NULL COMMENT 'Date-time of last update to this task_status record.',
  PRIMARY KEY (`id`),
  KEY `index_task_status_responsible` (`task_id`,`responsible_entity_table`,`responsible_entity_id`),
  KEY `index_task_status_target` (`task_id`,`target_entity_table`,`target_entity_id`),
  CONSTRAINT `FK_civicrm_task_status_task_id` FOREIGN KEY (`task_id`) REFERENCES `civicrm_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_tell_friend
# ------------------------------------------------------------

CREATE TABLE `civicrm_tell_friend` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Friend ID',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of table where item being referenced is stored.',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Foreign key to the referenced item.',
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `intro` text COLLATE utf8_unicode_ci COMMENT 'Introductory message to contributor or participant displayed on the Tell a Friend form.',
  `suggested_message` text COLLATE utf8_unicode_ci COMMENT 'Suggested message to friends, provided as default on the Tell A Friend form.',
  `general_link` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'URL for general info about the organization - included in the email sent to friends.',
  `thankyou_title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Text for Tell a Friend thank you page header and HTML title.',
  `thankyou_text` text COLLATE utf8_unicode_ci COMMENT 'Thank you message displayed on success page.',
  `is_active` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_timezone
# ------------------------------------------------------------

CREATE TABLE `civicrm_timezone` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Timezone Id',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Timezone full name',
  `abbreviation` char(3) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'ISO Code for timezone abbreviation',
  `gmt` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'GMT name of the timezone',
  `offset` int(11) DEFAULT NULL,
  `country_id` int(10) unsigned NOT NULL COMMENT 'Country Id',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_timezone_country_id` (`country_id`),
  CONSTRAINT `FK_civicrm_timezone_country_id` FOREIGN KEY (`country_id`) REFERENCES `civicrm_country` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_uf_field
# ------------------------------------------------------------

CREATE TABLE `civicrm_uf_field` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique table ID',
  `uf_group_id` int(10) unsigned NOT NULL COMMENT 'Which form does this field belong to.',
  `field_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name for CiviCRM field which is being exposed for sharing.',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this field currently shareable? If false, hide the field for all sharing contexts.',
  `is_view` tinyint(4) DEFAULT '0' COMMENT 'the field is view only and not editable in user forms.',
  `is_required` tinyint(4) DEFAULT '0' COMMENT 'Is this field required when included in a user or registration form?',
  `weight` int(11) NOT NULL DEFAULT '1' COMMENT 'Controls field display order when user framework fields are displayed in registration and account editing forms.',
  `help_post` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display after this field.',
  `help_pre` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display before this field.',
  `visibility` enum('User and User Admin Only','Public Pages','Public Pages and Listings') COLLATE utf8_unicode_ci DEFAULT 'User and User Admin Only' COMMENT 'In what context(s) is this field visible.',
  `in_selector` tinyint(4) DEFAULT '0' COMMENT 'Is this field included as a column in the selector table?',
  `is_searchable` tinyint(4) DEFAULT '0' COMMENT 'Is this field included search form of profile?',
  `location_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Location type of this mapping, if required',
  `phone_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Phone Type Id, if required',
  `label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'To save label for fields.',
  `field_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'This field saves field type (ie individual,household.. field etc).',
  `is_reserved` tinyint(4) DEFAULT NULL COMMENT 'Is this field reserved for use by some other CiviCRM functionality?',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_uf_field_uf_group_id` (`uf_group_id`),
  KEY `FK_civicrm_uf_field_location_type_id` (`location_type_id`),
  CONSTRAINT `FK_civicrm_uf_field_location_type_id` FOREIGN KEY (`location_type_id`) REFERENCES `civicrm_location_type` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_uf_field_uf_group_id` FOREIGN KEY (`uf_group_id`) REFERENCES `civicrm_uf_group` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_uf_group
# ------------------------------------------------------------

CREATE TABLE `civicrm_uf_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique table ID',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this form currently active? If false, hide all related fields for all sharing contexts.',
  `group_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'This column will store a comma separated list of the type(s) of profile fields.',
  `title` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Form title.',
  `help_pre` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display before fields in form.',
  `help_post` text COLLATE utf8_unicode_ci COMMENT 'Description and/or help text to display after fields in form.',
  `limit_listings_group_id` int(10) unsigned DEFAULT NULL COMMENT 'Group id, foriegn key from civicrm_group',
  `post_URL` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Redirect to URL.',
  `add_to_group_id` int(10) unsigned DEFAULT NULL COMMENT 'foreign key to civicrm_group_id',
  `add_captcha` tinyint(4) DEFAULT '0' COMMENT 'Should a CAPTCHA widget be included this Profile form.',
  `is_map` tinyint(4) DEFAULT '0' COMMENT 'Do we want to map results from this profile.',
  `is_edit_link` tinyint(4) DEFAULT '0' COMMENT 'Should edit link display in profile selector',
  `is_uf_link` tinyint(4) DEFAULT '0' COMMENT 'Should we display a link to the website profile in profile selector',
  `is_update_dupe` tinyint(4) DEFAULT '0' COMMENT 'Should we update the contact record if we find a duplicate',
  `cancel_URL` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Redirect to URL when Cancle button clik .',
  `is_cms_user` tinyint(4) DEFAULT '0' COMMENT 'Should we create a cms user for this profile ',
  `notify` text COLLATE utf8_unicode_ci,
  `is_reserved` tinyint(4) DEFAULT NULL COMMENT 'Is this group reserved for use by some other CiviCRM functionality?',
  `name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of the UF group for directly addressing it in the codebase',
  `created_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to civicrm_contact, who created this UF group',
  `created_date` datetime DEFAULT NULL COMMENT 'Date and time this UF group was created.',
  `is_proximity_search` tinyint(4) DEFAULT '0' COMMENT 'Should we include proximity search feature in this profile search form?',
  PRIMARY KEY (`id`),
  KEY `FK_civicrm_uf_group_limit_listings_group_id` (`limit_listings_group_id`),
  KEY `FK_civicrm_uf_group_add_to_group_id` (`add_to_group_id`),
  KEY `FK_civicrm_uf_group_created_id` (`created_id`),
  CONSTRAINT `FK_civicrm_uf_group_add_to_group_id` FOREIGN KEY (`add_to_group_id`) REFERENCES `civicrm_group` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_uf_group_created_id` FOREIGN KEY (`created_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_civicrm_uf_group_limit_listings_group_id` FOREIGN KEY (`limit_listings_group_id`) REFERENCES `civicrm_group` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_uf_join
# ------------------------------------------------------------

CREATE TABLE `civicrm_uf_join` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique table ID',
  `is_active` tinyint(4) DEFAULT '1' COMMENT 'Is this join currently active?',
  `module` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Module which owns this uf_join instance, e.g. User Registration, CiviDonate, etc.',
  `entity_table` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Name of table where item being referenced is stored. Modules which only need a single collection of uf_join instances may choose not to populate entity_table and entity_id.',
  `entity_id` int(10) unsigned DEFAULT NULL COMMENT 'Foreign key to the referenced item.',
  `weight` int(11) NOT NULL DEFAULT '1' COMMENT 'Controls display order when multiple user framework groups are setup for concurrent display.',
  `uf_group_id` int(10) unsigned NOT NULL COMMENT 'Which form does this field belong to.',
  PRIMARY KEY (`id`),
  KEY `index_entity` (`entity_table`,`entity_id`),
  KEY `FK_civicrm_uf_join_uf_group_id` (`uf_group_id`),
  CONSTRAINT `FK_civicrm_uf_join_uf_group_id` FOREIGN KEY (`uf_group_id`) REFERENCES `civicrm_uf_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_uf_match
# ------------------------------------------------------------

CREATE TABLE `civicrm_uf_match` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'System generated ID.',
  `domain_id` int(10) unsigned NOT NULL COMMENT 'Which Domain is this match entry for',
  `uf_id` int(10) unsigned NOT NULL COMMENT 'UF ID',
  `uf_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'UF Name',
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `language` varchar(5) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'UI language preferred by the given user/contact',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UI_uf_name_domain_id` (`uf_name`,`domain_id`),
  UNIQUE KEY `UI_contact_domain_id` (`contact_id`,`domain_id`),
  KEY `I_civicrm_uf_match_uf_id` (`uf_id`),
  KEY `FK_civicrm_uf_match_domain_id` (`domain_id`),
  CONSTRAINT `FK_civicrm_uf_match_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_civicrm_uf_match_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `civicrm_domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_value_db_info_6
# ------------------------------------------------------------

CREATE TABLE `civicrm_value_db_info_6` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Default MySQL primary key',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Table that this extends',
  `campusid_50` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_entity_id` (`entity_id`),
  KEY `INDEX_campusid_50` (`campusid_50`),
  CONSTRAINT `FK_civicrm_value_db_info_6_entity_id` FOREIGN KEY (`entity_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_value_indicated_decision_info_5
# ------------------------------------------------------------

CREATE TABLE `civicrm_value_indicated_decision_info_5` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Default MySQL primary key',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Table that this extends',
  `decision_date_45` datetime DEFAULT NULL,
  `witness_name_46` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `method_47` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `integrated__48` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `story_49` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_entity_id` (`entity_id`),
  KEY `INDEX_decision_date_45` (`decision_date_45`),
  KEY `INDEX_witness_name_46` (`witness_name_46`),
  KEY `INDEX_method_47` (`method_47`),
  KEY `INDEX_integrated__48` (`integrated__48`),
  CONSTRAINT `FK_civicrm_value_indicated_decision_a0726b68683a2b30` FOREIGN KEY (`entity_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_value_mycravings_fall_2012_7
# ------------------------------------------------------------

CREATE TABLE `civicrm_value_mycravings_fall_2012_7` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Default MySQL primary key',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Table that this extends',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_entity_id` (`entity_id`),
  CONSTRAINT `FK_civicrm_value_mycravings_fall_2012_7_entity_id` FOREIGN KEY (`entity_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_value_school_attending__dropdown__4
# ------------------------------------------------------------

CREATE TABLE `civicrm_value_school_attending__dropdown__4` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Default MySQL primary key',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Table that this extends',
  `currently_attending_25` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_entity_id` (`entity_id`),
  KEY `INDEX_currently_attending_25` (`currently_attending_25`),
  CONSTRAINT `FK_civicrm_value_school_attending___2ba9a4fdc2dc6c78` FOREIGN KEY (`entity_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_value_school_info_10
# ------------------------------------------------------------

CREATE TABLE `civicrm_value_school_info_10` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Default MySQL primary key',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Table that this extends',
  `slm_q1_68` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `slm_q2_69` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `slm_q3_70` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `is_it_slm_71` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `staffed_or_exp__72` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `do_we_have_a_ministry_presence_h_73` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sponsor_or_launch_74` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_entity_id` (`entity_id`),
  KEY `INDEX_slm_q2_69` (`slm_q2_69`),
  KEY `INDEX_slm_q3_70` (`slm_q3_70`),
  KEY `INDEX_is_it_slm_71` (`is_it_slm_71`),
  KEY `INDEX_staffed_or_exp__72` (`staffed_or_exp__72`),
  KEY `INDEX_do_we_have_a_ministry_presence_h_73` (`do_we_have_a_ministry_presence_h_73`),
  KEY `INDEX_sponsor_or_launch_74` (`sponsor_or_launch_74`),
  KEY `INDEX_slm_q1_68` (`slm_q1_68`),
  CONSTRAINT `FK_civicrm_value_school_info_10_entity_id` FOREIGN KEY (`entity_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_value_sept_launch_2012_9
# ------------------------------------------------------------

CREATE TABLE `civicrm_value_sept_launch_2012_9` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Default MySQL primary key',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Table that this extends',
  `the_one_thing_i_crave_most_is_64` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `i_d_like_a_free_magazine_by_pers_65` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `power_to_change_loves_to_help_st_66` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `on_my_spiritual_journey_i_d_like_67` varchar(255) COLLATE utf8_unicode_ci DEFAULT 'explore',
  `followup_piority_75` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `segment_76` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `action_77` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `action_status_78` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `data_input_notes_83` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_entity_id` (`entity_id`),
  KEY `INDEX_i_d_like_a_free_magazine_by_pers_65` (`i_d_like_a_free_magazine_by_pers_65`),
  KEY `INDEX_on_my_spiritual_journey_i_d_like_67` (`on_my_spiritual_journey_i_d_like_67`),
  KEY `INDEX_followup_piority_75` (`followup_piority_75`),
  KEY `INDEX_segment_76` (`segment_76`),
  KEY `INDEX_power_to_change_loves_to_help_st_66` (`power_to_change_loves_to_help_st_66`),
  KEY `INDEX_the_one_thing_i_crave_most_is_64` (`the_one_thing_i_crave_most_is_64`),
  KEY `INDEX_action_77` (`action_77`),
  KEY `INDEX_action_status_78` (`action_status_78`),
  CONSTRAINT `FK_civicrm_value_sept_launch_2012_9_entity_id` FOREIGN KEY (`entity_id`) REFERENCES `civicrm_activity` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_value_student_demographics_7
# ------------------------------------------------------------

CREATE TABLE `civicrm_value_student_demographics_7` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Default MySQL primary key',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Table that this extends',
  `year_in_school_57` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `year__other_58` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `faculty_or_degree_59` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `on_campus_residence_60` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `i_am_an_international_student_61` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `short_link_to_text_80` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email_or_text_sent__september_la_81` tinyint(4) DEFAULT NULL,
  `imported_to_pulse_82` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_entity_id` (`entity_id`),
  KEY `INDEX_year_in_school_57` (`year_in_school_57`),
  KEY `INDEX_year__other_58` (`year__other_58`),
  KEY `INDEX_on_campus_residence_60` (`on_campus_residence_60`),
  KEY `INDEX_i_am_an_international_student_61` (`i_am_an_international_student_61`),
  KEY `INDEX_short_link_to_text_80` (`short_link_to_text_80`),
  KEY `INDEX_faculty_or_degree_59` (`faculty_or_degree_59`),
  KEY `INDEX_email_or_text_sent__september_la_81` (`email_or_text_sent__september_la_81`),
  KEY `INDEX_imported_to_pulse_82` (`imported_to_pulse_82`),
  CONSTRAINT `FK_civicrm_value_student_demographics_7_entity_id` FOREIGN KEY (`entity_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_value_survey_one__english__2
# ------------------------------------------------------------

CREATE TABLE `civicrm_value_survey_one__english__2` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Default MySQL primary key',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'Table that this extends',
  `please_specify__10` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `free_magazine_to_explore_craving_11` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `i_m_a_person_who_12` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `would_you_like_to_find_out_how_t_13` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `your_residence_14` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keep_updataed_15` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `help_others_discover_jesus__16` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `other_17` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `facility_degree_18` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `year_19` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `notes_30` text COLLATE utf8_unicode_ci,
  `if_on_campus_which_residence__32` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `filterlevel_37` int(11) DEFAULT NULL,
  `followupstatus_38` int(11) DEFAULT '1',
  `student_communities_that_best_de_39` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `international_student__40` tinyint(4) DEFAULT NULL,
  `activity_status_41` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `assigned_to_42` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `follow_up_notes_43` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_entity_id` (`entity_id`),
  KEY `INDEX_please_specify__10` (`please_specify__10`),
  KEY `INDEX_i_m_a_person_who_12` (`i_m_a_person_who_12`),
  KEY `INDEX_would_you_like_to_find_out_how_t_13` (`would_you_like_to_find_out_how_t_13`),
  KEY `INDEX_your_residence_14` (`your_residence_14`),
  KEY `INDEX_keep_updataed_15` (`keep_updataed_15`),
  KEY `INDEX_help_others_discover_jesus__16` (`help_others_discover_jesus__16`),
  KEY `INDEX_other_17` (`other_17`),
  KEY `INDEX_facility_degree_18` (`facility_degree_18`),
  KEY `INDEX_year_19` (`year_19`),
  KEY `INDEX_filterlevel_37` (`filterlevel_37`),
  KEY `INDEX_followupstatus_38` (`followupstatus_38`),
  KEY `INDEX_student_communities_that_best_de_39` (`student_communities_that_best_de_39`),
  KEY `INDEX_international_student__40` (`international_student__40`),
  KEY `INDEX_free_magazine_to_explore_craving_11` (`free_magazine_to_explore_craving_11`),
  KEY `INDEX_activity_status_41` (`activity_status_41`),
  KEY `INDEX_assigned_to_42` (`assigned_to_42`),
  CONSTRAINT `FK_civicrm_value_survey_one__english__2_entity_id` FOREIGN KEY (`entity_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_website
# ------------------------------------------------------------

CREATE TABLE `civicrm_website` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique Website ID',
  `contact_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to Contact ID',
  `url` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Website',
  `website_type_id` int(10) unsigned DEFAULT NULL COMMENT 'Which Website type does this website belong to.',
  PRIMARY KEY (`id`),
  KEY `UI_website_type_id` (`website_type_id`),
  KEY `FK_civicrm_website_contact_id` (`contact_id`),
  CONSTRAINT `FK_civicrm_website_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `civicrm_contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table civicrm_worldregion
# ------------------------------------------------------------

CREATE TABLE `civicrm_worldregion` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Country Id',
  `name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Region name to be associated with countries',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table comment
# ------------------------------------------------------------

CREATE TABLE `comment` (
  `cid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key: Unique comment ID.',
  `pid` int(11) NOT NULL DEFAULT '0' COMMENT 'The comment.cid to which this comment is a reply. If set to 0, this comment is not a reply to an existing comment.',
  `nid` int(11) NOT NULL DEFAULT '0' COMMENT 'The node.nid to which this comment is a reply.',
  `uid` int(11) NOT NULL DEFAULT '0' COMMENT 'The users.uid who authored the comment. If set to 0, this comment was created by an anonymous user.',
  `subject` varchar(64) NOT NULL DEFAULT '' COMMENT 'The comment title.',
  `hostname` varchar(128) NOT NULL DEFAULT '' COMMENT 'The authorâ€™s host name.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'The time that the comment was created, as a Unix timestamp.',
  `changed` int(11) NOT NULL DEFAULT '0' COMMENT 'The time that the comment was last edited, as a Unix timestamp.',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'The published status of a comment. (0 = Not Published, 1 = Published)',
  `thread` varchar(255) NOT NULL COMMENT 'The vancode representation of the commentâ€™s place in a thread.',
  `name` varchar(60) DEFAULT NULL COMMENT 'The comment authorâ€™s name. Uses users.name if the user is logged in, otherwise uses the value typed into the comment form.',
  `mail` varchar(64) DEFAULT NULL COMMENT 'The comment authorâ€™s e-mail address from the comment form, if user is anonymous, and the â€™Anonymous users may/must leave their contact informationâ€™ setting is turned on.',
  `homepage` varchar(255) DEFAULT NULL COMMENT 'The comment authorâ€™s home page address from the comment form, if user is anonymous, and the â€™Anonymous users may/must leave their contact informationâ€™ setting is turned on.',
  `language` varchar(12) NOT NULL DEFAULT '' COMMENT 'The languages.language of this comment.',
  PRIMARY KEY (`cid`),
  KEY `comment_status_pid` (`pid`,`status`),
  KEY `comment_num_new` (`nid`,`status`,`created`,`cid`,`thread`),
  KEY `comment_uid` (`uid`),
  KEY `comment_nid_language` (`nid`,`language`),
  KEY `comment_created` (`created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores comments and associated data.';



# Dump of table ctools_access_ruleset
# ------------------------------------------------------------

CREATE TABLE `ctools_access_ruleset` (
  `rsid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'A database primary key to ensure uniqueness',
  `name` varchar(255) DEFAULT NULL COMMENT 'Unique ID for this ruleset. Used to identify it programmatically.',
  `admin_title` varchar(255) DEFAULT NULL COMMENT 'Administrative title for this ruleset.',
  `admin_description` longtext COMMENT 'Administrative description for this ruleset.',
  `requiredcontexts` longtext COMMENT 'Any required contexts for this ruleset.',
  `contexts` longtext COMMENT 'Any embedded contexts for this ruleset.',
  `relationships` longtext COMMENT 'Any relationships for this ruleset.',
  `access` longtext COMMENT 'The actual group of access plugins for this ruleset.',
  PRIMARY KEY (`rsid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Contains exportable customized access rulesets.';



# Dump of table ctools_css_cache
# ------------------------------------------------------------

CREATE TABLE `ctools_css_cache` (
  `cid` varchar(128) NOT NULL COMMENT 'The CSS ID this cache object belongs to.',
  `filename` varchar(255) DEFAULT NULL COMMENT 'The filename this CSS is stored in.',
  `css` longtext COMMENT 'CSS being stored.',
  `filter` tinyint(4) DEFAULT NULL COMMENT 'Whether or not this CSS needs to be filtered.',
  PRIMARY KEY (`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='A special cache used to store CSS that must be non-volatile.';



# Dump of table ctools_custom_content
# ------------------------------------------------------------

CREATE TABLE `ctools_custom_content` (
  `cid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'A database primary key to ensure uniqueness',
  `name` varchar(255) DEFAULT NULL COMMENT 'Unique ID for this content. Used to identify it programmatically.',
  `admin_title` varchar(255) DEFAULT NULL COMMENT 'Administrative title for this content.',
  `admin_description` longtext COMMENT 'Administrative description for this content.',
  `category` varchar(255) DEFAULT NULL COMMENT 'Administrative category for this content.',
  `settings` longtext COMMENT 'Serialized settings for the actual content to be used',
  PRIMARY KEY (`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Contains exportable customized content for this site.';



# Dump of table ctools_object_cache
# ------------------------------------------------------------

CREATE TABLE `ctools_object_cache` (
  `sid` varchar(64) NOT NULL COMMENT 'The session ID this cache object belongs to.',
  `name` varchar(128) NOT NULL COMMENT 'The name of the object this cache is attached to.',
  `obj` varchar(32) NOT NULL COMMENT 'The type of the object this cache is attached to; this essentially represents the owner so that several sub-systems can use this cache.',
  `updated` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The time this cache was created or updated.',
  `data` longtext COMMENT 'Serialized data being stored.',
  PRIMARY KEY (`sid`,`obj`,`name`),
  KEY `updated` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='A special cache used to store objects that are being...';



# Dump of table date_format_locale
# ------------------------------------------------------------

CREATE TABLE `date_format_locale` (
  `format` varchar(100) NOT NULL COMMENT 'The date format string.',
  `type` varchar(64) NOT NULL COMMENT 'The date format type, e.g. medium.',
  `language` varchar(12) NOT NULL COMMENT 'A languages.language for this format to be used with.',
  PRIMARY KEY (`type`,`language`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores configured date formats for each locale.';



# Dump of table date_format_type
# ------------------------------------------------------------

CREATE TABLE `date_format_type` (
  `type` varchar(64) NOT NULL COMMENT 'The date format type, e.g. medium.',
  `title` varchar(255) NOT NULL COMMENT 'The human readable name of the format type.',
  `locked` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Whether or not this is a system provided format.',
  PRIMARY KEY (`type`),
  KEY `title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores configured date format types.';



# Dump of table date_formats
# ------------------------------------------------------------

CREATE TABLE `date_formats` (
  `dfid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The date format identifier.',
  `format` varchar(100) NOT NULL COMMENT 'The date format string.',
  `type` varchar(64) NOT NULL COMMENT 'The date format type, e.g. medium.',
  `locked` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Whether or not this format can be modified.',
  PRIMARY KEY (`dfid`),
  UNIQUE KEY `formats` (`format`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores configured date formats.';



# Dump of table field_config
# ------------------------------------------------------------

CREATE TABLE `field_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The primary identifier for a field',
  `field_name` varchar(32) NOT NULL COMMENT 'The name of this field. Non-deleted field names are unique, but multiple deleted fields can have the same name.',
  `type` varchar(128) NOT NULL COMMENT 'The type of this field.',
  `module` varchar(128) NOT NULL DEFAULT '' COMMENT 'The module that implements the field type.',
  `active` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether the module that implements the field type is enabled.',
  `storage_type` varchar(128) NOT NULL COMMENT 'The storage backend for the field.',
  `storage_module` varchar(128) NOT NULL DEFAULT '' COMMENT 'The module that implements the storage backend.',
  `storage_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether the module that implements the storage backend is enabled.',
  `locked` tinyint(4) NOT NULL DEFAULT '0' COMMENT '@TODO',
  `data` longblob NOT NULL COMMENT 'Serialized data containing the field properties that do not warrant a dedicated column.',
  `cardinality` tinyint(4) NOT NULL DEFAULT '0',
  `translatable` tinyint(4) NOT NULL DEFAULT '0',
  `deleted` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `field_name` (`field_name`),
  KEY `active` (`active`),
  KEY `storage_active` (`storage_active`),
  KEY `deleted` (`deleted`),
  KEY `module` (`module`),
  KEY `storage_module` (`storage_module`),
  KEY `type` (`type`),
  KEY `storage_type` (`storage_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table field_config_instance
# ------------------------------------------------------------

CREATE TABLE `field_config_instance` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The primary identifier for a field instance',
  `field_id` int(11) NOT NULL COMMENT 'The identifier of the field attached by this instance',
  `field_name` varchar(32) NOT NULL DEFAULT '',
  `entity_type` varchar(32) NOT NULL DEFAULT '',
  `bundle` varchar(128) NOT NULL DEFAULT '',
  `data` longblob NOT NULL,
  `deleted` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `field_name_bundle` (`field_name`,`entity_type`,`bundle`),
  KEY `deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table field_data_body
# ------------------------------------------------------------

CREATE TABLE `field_data_body` (
  `entity_type` varchar(128) NOT NULL DEFAULT '' COMMENT 'The entity type this data is attached to',
  `bundle` varchar(128) NOT NULL DEFAULT '' COMMENT 'The field instance bundle to which this row belongs, used when deleting a field instance',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether this data item has been deleted',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'The entity id this data is attached to',
  `revision_id` int(10) unsigned DEFAULT NULL COMMENT 'The entity revision id this data is attached to, or NULL if the entity type is not versioned',
  `language` varchar(32) NOT NULL DEFAULT '' COMMENT 'The language for this data item.',
  `delta` int(10) unsigned NOT NULL COMMENT 'The sequence number for this data item, used for multi-value fields',
  `body_value` longtext,
  `body_summary` longtext,
  `body_format` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`entity_type`,`entity_id`,`deleted`,`delta`,`language`),
  KEY `entity_type` (`entity_type`),
  KEY `bundle` (`bundle`),
  KEY `deleted` (`deleted`),
  KEY `entity_id` (`entity_id`),
  KEY `revision_id` (`revision_id`),
  KEY `language` (`language`),
  KEY `body_format` (`body_format`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Data storage for field 2 (body)';



# Dump of table field_data_comment_body
# ------------------------------------------------------------

CREATE TABLE `field_data_comment_body` (
  `entity_type` varchar(128) NOT NULL DEFAULT '' COMMENT 'The entity type this data is attached to',
  `bundle` varchar(128) NOT NULL DEFAULT '' COMMENT 'The field instance bundle to which this row belongs, used when deleting a field instance',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether this data item has been deleted',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'The entity id this data is attached to',
  `revision_id` int(10) unsigned DEFAULT NULL COMMENT 'The entity revision id this data is attached to, or NULL if the entity type is not versioned',
  `language` varchar(32) NOT NULL DEFAULT '' COMMENT 'The language for this data item.',
  `delta` int(10) unsigned NOT NULL COMMENT 'The sequence number for this data item, used for multi-value fields',
  `comment_body_value` longtext,
  `comment_body_format` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`entity_type`,`entity_id`,`deleted`,`delta`,`language`),
  KEY `entity_type` (`entity_type`),
  KEY `bundle` (`bundle`),
  KEY `deleted` (`deleted`),
  KEY `entity_id` (`entity_id`),
  KEY `revision_id` (`revision_id`),
  KEY `language` (`language`),
  KEY `comment_body_format` (`comment_body_format`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Data storage for field 1 (comment_body)';



# Dump of table field_data_field_image
# ------------------------------------------------------------

CREATE TABLE `field_data_field_image` (
  `entity_type` varchar(128) NOT NULL DEFAULT '' COMMENT 'The entity type this data is attached to',
  `bundle` varchar(128) NOT NULL DEFAULT '' COMMENT 'The field instance bundle to which this row belongs, used when deleting a field instance',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether this data item has been deleted',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'The entity id this data is attached to',
  `revision_id` int(10) unsigned DEFAULT NULL COMMENT 'The entity revision id this data is attached to, or NULL if the entity type is not versioned',
  `language` varchar(32) NOT NULL DEFAULT '' COMMENT 'The language for this data item.',
  `delta` int(10) unsigned NOT NULL COMMENT 'The sequence number for this data item, used for multi-value fields',
  `field_image_fid` int(10) unsigned DEFAULT NULL COMMENT 'The file_managed.fid being referenced in this field.',
  `field_image_alt` varchar(512) DEFAULT NULL,
  `field_image_title` varchar(1024) DEFAULT NULL,
  `field_image_width` int(10) unsigned DEFAULT NULL COMMENT 'The width of the image in pixels.',
  `field_image_height` int(10) unsigned DEFAULT NULL COMMENT 'The height of the image in pixels.',
  PRIMARY KEY (`entity_type`,`entity_id`,`deleted`,`delta`,`language`),
  KEY `entity_type` (`entity_type`),
  KEY `bundle` (`bundle`),
  KEY `deleted` (`deleted`),
  KEY `entity_id` (`entity_id`),
  KEY `revision_id` (`revision_id`),
  KEY `language` (`language`),
  KEY `field_image_fid` (`field_image_fid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Data storage for field 4 (field_image)';



# Dump of table field_data_field_tags
# ------------------------------------------------------------

CREATE TABLE `field_data_field_tags` (
  `entity_type` varchar(128) NOT NULL DEFAULT '' COMMENT 'The entity type this data is attached to',
  `bundle` varchar(128) NOT NULL DEFAULT '' COMMENT 'The field instance bundle to which this row belongs, used when deleting a field instance',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether this data item has been deleted',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'The entity id this data is attached to',
  `revision_id` int(10) unsigned DEFAULT NULL COMMENT 'The entity revision id this data is attached to, or NULL if the entity type is not versioned',
  `language` varchar(32) NOT NULL DEFAULT '' COMMENT 'The language for this data item.',
  `delta` int(10) unsigned NOT NULL COMMENT 'The sequence number for this data item, used for multi-value fields',
  `field_tags_tid` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`entity_type`,`entity_id`,`deleted`,`delta`,`language`),
  KEY `entity_type` (`entity_type`),
  KEY `bundle` (`bundle`),
  KEY `deleted` (`deleted`),
  KEY `entity_id` (`entity_id`),
  KEY `revision_id` (`revision_id`),
  KEY `language` (`language`),
  KEY `field_tags_tid` (`field_tags_tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Data storage for field 3 (field_tags)';



# Dump of table field_revision_body
# ------------------------------------------------------------

CREATE TABLE `field_revision_body` (
  `entity_type` varchar(128) NOT NULL DEFAULT '' COMMENT 'The entity type this data is attached to',
  `bundle` varchar(128) NOT NULL DEFAULT '' COMMENT 'The field instance bundle to which this row belongs, used when deleting a field instance',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether this data item has been deleted',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'The entity id this data is attached to',
  `revision_id` int(10) unsigned NOT NULL COMMENT 'The entity revision id this data is attached to',
  `language` varchar(32) NOT NULL DEFAULT '' COMMENT 'The language for this data item.',
  `delta` int(10) unsigned NOT NULL COMMENT 'The sequence number for this data item, used for multi-value fields',
  `body_value` longtext,
  `body_summary` longtext,
  `body_format` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`entity_type`,`entity_id`,`revision_id`,`deleted`,`delta`,`language`),
  KEY `entity_type` (`entity_type`),
  KEY `bundle` (`bundle`),
  KEY `deleted` (`deleted`),
  KEY `entity_id` (`entity_id`),
  KEY `revision_id` (`revision_id`),
  KEY `language` (`language`),
  KEY `body_format` (`body_format`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Revision archive storage for field 2 (body)';



# Dump of table field_revision_comment_body
# ------------------------------------------------------------

CREATE TABLE `field_revision_comment_body` (
  `entity_type` varchar(128) NOT NULL DEFAULT '' COMMENT 'The entity type this data is attached to',
  `bundle` varchar(128) NOT NULL DEFAULT '' COMMENT 'The field instance bundle to which this row belongs, used when deleting a field instance',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether this data item has been deleted',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'The entity id this data is attached to',
  `revision_id` int(10) unsigned NOT NULL COMMENT 'The entity revision id this data is attached to',
  `language` varchar(32) NOT NULL DEFAULT '' COMMENT 'The language for this data item.',
  `delta` int(10) unsigned NOT NULL COMMENT 'The sequence number for this data item, used for multi-value fields',
  `comment_body_value` longtext,
  `comment_body_format` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`entity_type`,`entity_id`,`revision_id`,`deleted`,`delta`,`language`),
  KEY `entity_type` (`entity_type`),
  KEY `bundle` (`bundle`),
  KEY `deleted` (`deleted`),
  KEY `entity_id` (`entity_id`),
  KEY `revision_id` (`revision_id`),
  KEY `language` (`language`),
  KEY `comment_body_format` (`comment_body_format`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Revision archive storage for field 1 (comment_body)';



# Dump of table field_revision_field_image
# ------------------------------------------------------------

CREATE TABLE `field_revision_field_image` (
  `entity_type` varchar(128) NOT NULL DEFAULT '' COMMENT 'The entity type this data is attached to',
  `bundle` varchar(128) NOT NULL DEFAULT '' COMMENT 'The field instance bundle to which this row belongs, used when deleting a field instance',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether this data item has been deleted',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'The entity id this data is attached to',
  `revision_id` int(10) unsigned NOT NULL COMMENT 'The entity revision id this data is attached to',
  `language` varchar(32) NOT NULL DEFAULT '' COMMENT 'The language for this data item.',
  `delta` int(10) unsigned NOT NULL COMMENT 'The sequence number for this data item, used for multi-value fields',
  `field_image_fid` int(10) unsigned DEFAULT NULL COMMENT 'The file_managed.fid being referenced in this field.',
  `field_image_alt` varchar(512) DEFAULT NULL,
  `field_image_title` varchar(1024) DEFAULT NULL,
  `field_image_width` int(10) unsigned DEFAULT NULL COMMENT 'The width of the image in pixels.',
  `field_image_height` int(10) unsigned DEFAULT NULL COMMENT 'The height of the image in pixels.',
  PRIMARY KEY (`entity_type`,`entity_id`,`revision_id`,`deleted`,`delta`,`language`),
  KEY `entity_type` (`entity_type`),
  KEY `bundle` (`bundle`),
  KEY `deleted` (`deleted`),
  KEY `entity_id` (`entity_id`),
  KEY `revision_id` (`revision_id`),
  KEY `language` (`language`),
  KEY `field_image_fid` (`field_image_fid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Revision archive storage for field 4 (field_image)';



# Dump of table field_revision_field_tags
# ------------------------------------------------------------

CREATE TABLE `field_revision_field_tags` (
  `entity_type` varchar(128) NOT NULL DEFAULT '' COMMENT 'The entity type this data is attached to',
  `bundle` varchar(128) NOT NULL DEFAULT '' COMMENT 'The field instance bundle to which this row belongs, used when deleting a field instance',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether this data item has been deleted',
  `entity_id` int(10) unsigned NOT NULL COMMENT 'The entity id this data is attached to',
  `revision_id` int(10) unsigned NOT NULL COMMENT 'The entity revision id this data is attached to',
  `language` varchar(32) NOT NULL DEFAULT '' COMMENT 'The language for this data item.',
  `delta` int(10) unsigned NOT NULL COMMENT 'The sequence number for this data item, used for multi-value fields',
  `field_tags_tid` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`entity_type`,`entity_id`,`revision_id`,`deleted`,`delta`,`language`),
  KEY `entity_type` (`entity_type`),
  KEY `bundle` (`bundle`),
  KEY `deleted` (`deleted`),
  KEY `entity_id` (`entity_id`),
  KEY `revision_id` (`revision_id`),
  KEY `language` (`language`),
  KEY `field_tags_tid` (`field_tags_tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Revision archive storage for field 3 (field_tags)';



# Dump of table file_managed
# ------------------------------------------------------------

CREATE TABLE `file_managed` (
  `fid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'File ID.',
  `uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The users.uid of the user who is associated with the file.',
  `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Name of the file with no path components. This may differ from the basename of the URI if the file is renamed to avoid overwriting an existing file.',
  `uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'The URI to access the file (either local or remote).',
  `filemime` varchar(255) NOT NULL DEFAULT '' COMMENT 'The fileâ€™s MIME type.',
  `filesize` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The size of the file in bytes.',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A field indicating the status of the file. Two status are defined in core: temporary (0) and permanent (1). Temporary files older than DRUPAL_MAXIMUM_TEMP_FILE_AGE will be removed during a cron run.',
  `timestamp` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'UNIX timestamp for when the file was added.',
  PRIMARY KEY (`fid`),
  UNIQUE KEY `uri` (`uri`),
  KEY `uid` (`uid`),
  KEY `status` (`status`),
  KEY `timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores information for uploaded files.';



# Dump of table file_usage
# ------------------------------------------------------------

CREATE TABLE `file_usage` (
  `fid` int(10) unsigned NOT NULL COMMENT 'File ID.',
  `module` varchar(255) NOT NULL DEFAULT '' COMMENT 'The name of the module that is using the file.',
  `type` varchar(64) NOT NULL DEFAULT '' COMMENT 'The name of the object type in which the file is used.',
  `id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The primary key of the object using the file.',
  `count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The number of times this file is used by this object.',
  PRIMARY KEY (`fid`,`type`,`id`,`module`),
  KEY `type_id` (`type`,`id`),
  KEY `fid_count` (`fid`,`count`),
  KEY `fid_module` (`fid`,`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Track where a file is used.';



# Dump of table filter
# ------------------------------------------------------------

CREATE TABLE `filter` (
  `format` varchar(255) NOT NULL COMMENT 'Foreign key: The filter_format.format to which this filter is assigned.',
  `module` varchar(64) NOT NULL DEFAULT '' COMMENT 'The origin module of the filter.',
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT 'Name of the filter being referenced.',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'Weight of filter within format.',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT 'Filter enabled status. (1 = enabled, 0 = disabled)',
  `settings` longblob COMMENT 'A serialized array of name value pairs that store the filter settings for the specific format.',
  PRIMARY KEY (`format`,`name`),
  KEY `list` (`weight`,`module`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table that maps filters (HTML corrector) to text formats ...';



# Dump of table filter_format
# ------------------------------------------------------------

CREATE TABLE `filter_format` (
  `format` varchar(255) NOT NULL COMMENT 'Primary Key: Unique machine name of the format.',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'Name of the text format (Filtered HTML).',
  `cache` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Flag to indicate whether format is cacheable. (1 = cacheable, 0 = not cacheable)',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'The status of the text format. (1 = enabled, 0 = disabled)',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'Weight of text format to use when listing.',
  PRIMARY KEY (`format`),
  UNIQUE KEY `name` (`name`),
  KEY `status_weight` (`status`,`weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores text formats: custom groupings of filters, such as...';



# Dump of table flood
# ------------------------------------------------------------

CREATE TABLE `flood` (
  `fid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Unique flood event ID.',
  `event` varchar(64) NOT NULL DEFAULT '' COMMENT 'Name of event (e.g. contact).',
  `identifier` varchar(128) NOT NULL DEFAULT '' COMMENT 'Identifier of the visitor, such as an IP address or hostname.',
  `timestamp` int(11) NOT NULL DEFAULT '0' COMMENT 'Timestamp of the event.',
  `expiration` int(11) NOT NULL DEFAULT '0' COMMENT 'Expiration timestamp. Expired events are purged on cron run.',
  PRIMARY KEY (`fid`),
  KEY `allow` (`event`,`identifier`,`timestamp`),
  KEY `purge` (`expiration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Flood controls the threshold of events, such as the...';



# Dump of table front_page
# ------------------------------------------------------------

CREATE TABLE `front_page` (
  `rid` int(10) unsigned NOT NULL COMMENT 'Primary Key: Role ID.',
  `mode` varchar(10) NOT NULL DEFAULT '' COMMENT 'The mode the front page will operate in for the selected role.',
  `data` longtext NOT NULL COMMENT 'Contains the data for the selected mode. This could be a path or html to display.',
  `filter_format` varchar(255) NOT NULL DEFAULT '' COMMENT 'The filter format to apply to the data.',
  `weight` int(11) DEFAULT '0' COMMENT 'The weight of the front page setting.',
  PRIMARY KEY (`rid`),
  KEY `weight` (`weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores Front Page settings.';



# Dump of table history
# ------------------------------------------------------------

CREATE TABLE `history` (
  `uid` int(11) NOT NULL DEFAULT '0' COMMENT 'The users.uid that read the node nid.',
  `nid` int(11) NOT NULL DEFAULT '0' COMMENT 'The node.nid that was read.',
  `timestamp` int(11) NOT NULL DEFAULT '0' COMMENT 'The Unix timestamp at which the read occurred.',
  PRIMARY KEY (`uid`,`nid`),
  KEY `nid` (`nid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='A record of which users have read which nodes.';



# Dump of table image_effects
# ------------------------------------------------------------

CREATE TABLE `image_effects` (
  `ieid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The primary identifier for an image effect.',
  `isid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The image_styles.isid for an image style.',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'The weight of the effect in the style.',
  `name` varchar(255) NOT NULL COMMENT 'The unique name of the effect to be executed.',
  `data` longblob NOT NULL COMMENT 'The configuration data for the effect.',
  PRIMARY KEY (`ieid`),
  KEY `isid` (`isid`),
  KEY `weight` (`weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores configuration options for image effects.';



# Dump of table image_styles
# ------------------------------------------------------------

CREATE TABLE `image_styles` (
  `isid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The primary identifier for an image style.',
  `name` varchar(255) NOT NULL COMMENT 'The style name.',
  PRIMARY KEY (`isid`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores configuration options for image styles.';



# Dump of table languages
# ------------------------------------------------------------

CREATE TABLE `languages` (
  `language` varchar(12) NOT NULL DEFAULT '' COMMENT 'Language code, e.g. â€™deâ€™ or â€™en-USâ€™.',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT 'Language name in English.',
  `native` varchar(64) NOT NULL DEFAULT '' COMMENT 'Native language name.',
  `direction` int(11) NOT NULL DEFAULT '0' COMMENT 'Direction of language (Left-to-Right = 0, Right-to-Left = 1).',
  `enabled` int(11) NOT NULL DEFAULT '0' COMMENT 'Enabled flag (1 = Enabled, 0 = Disabled).',
  `plurals` int(11) NOT NULL DEFAULT '0' COMMENT 'Number of plural indexes in this language.',
  `formula` varchar(128) NOT NULL DEFAULT '' COMMENT 'Plural formula in PHP code to evaluate to get plural indexes.',
  `domain` varchar(128) NOT NULL DEFAULT '' COMMENT 'Domain to use for this language.',
  `prefix` varchar(128) NOT NULL DEFAULT '' COMMENT 'Path prefix to use for this language.',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'Weight, used in lists of languages.',
  `javascript` varchar(64) NOT NULL DEFAULT '' COMMENT 'Location of JavaScript translation file.',
  PRIMARY KEY (`language`),
  KEY `list` (`weight`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='List of all available languages in the system.';



# Dump of table locales_source
# ------------------------------------------------------------

CREATE TABLE `locales_source` (
  `lid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Unique identifier of this string.',
  `location` longtext COMMENT 'Drupal path in case of online discovered translations or file path in case of imported strings.',
  `textgroup` varchar(255) NOT NULL DEFAULT 'default' COMMENT 'A module defined group of translations, see hook_locale().',
  `source` blob NOT NULL COMMENT 'The original string in English.',
  `context` varchar(255) NOT NULL DEFAULT '' COMMENT 'The context this string applies to.',
  `version` varchar(20) NOT NULL DEFAULT 'none' COMMENT 'Version of Drupal, where the string was last used (for locales optimization).',
  PRIMARY KEY (`lid`),
  KEY `source_context` (`source`(30),`context`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='List of English source strings.';



# Dump of table locales_target
# ------------------------------------------------------------

CREATE TABLE `locales_target` (
  `lid` int(11) NOT NULL DEFAULT '0' COMMENT 'Source string ID. References locales_source.lid.',
  `translation` blob NOT NULL COMMENT 'Translation string value in this language.',
  `language` varchar(12) NOT NULL DEFAULT '' COMMENT 'Language code. References languages.language.',
  `plid` int(11) NOT NULL DEFAULT '0' COMMENT 'Parent lid (lid of the previous string in the plural chain) in case of plural strings. References locales_source.lid.',
  `plural` int(11) NOT NULL DEFAULT '0' COMMENT 'Plural index number in case of plural strings.',
  PRIMARY KEY (`language`,`lid`,`plural`),
  KEY `lid` (`lid`),
  KEY `plid` (`plid`),
  KEY `plural` (`plural`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores translated versions of strings.';



# Dump of table login_destination
# ------------------------------------------------------------

CREATE TABLE `login_destination` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key: Unique ID.',
  `triggers` text NOT NULL COMMENT 'Triggers on which to perform redirect',
  `roles` text NOT NULL COMMENT 'Roles to perform redirect for',
  `pages_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Flag to indicate from which pages to redirect. (0 = all pages except listed pages, 1 = only listed pages, 2 = Use custom PHP code)',
  `pages` text NOT NULL COMMENT 'Pages from which to redirect',
  `destination_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Flag to indicate the destination type. (0 = static URL, 1 = PHP code)',
  `destination` text NOT NULL COMMENT 'Redirect destination',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'The ruleâ€™s weight.',
  PRIMARY KEY (`id`),
  KEY `list` (`weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Login Destination rules.';



# Dump of table menu_custom
# ------------------------------------------------------------

CREATE TABLE `menu_custom` (
  `menu_name` varchar(32) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique key for menu. This is used as a block delta so length is 32.',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT 'Menu title; displayed at top of block.',
  `description` text COMMENT 'Menu description.',
  PRIMARY KEY (`menu_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds definitions for top-level custom menus (for example...';



# Dump of table menu_links
# ------------------------------------------------------------

CREATE TABLE `menu_links` (
  `menu_name` varchar(32) NOT NULL DEFAULT '' COMMENT 'The menu name. All links with the same menu name (such as â€™navigationâ€™) are part of the same menu.',
  `mlid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The menu link ID (mlid) is the integer primary key.',
  `plid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The parent link ID (plid) is the mlid of the link above in the hierarchy, or zero if the link is at the top level in its menu.',
  `link_path` varchar(255) NOT NULL DEFAULT '' COMMENT 'The Drupal path or external path this link points to.',
  `router_path` varchar(255) NOT NULL DEFAULT '' COMMENT 'For links corresponding to a Drupal path (external = 0), this connects the link to a menu_router.path for joins.',
  `link_title` varchar(255) NOT NULL DEFAULT '' COMMENT 'The text displayed for the link, which may be modified by a title callback stored in menu_router.',
  `options` blob COMMENT 'A serialized array of options to be passed to the url() or l() function, such as a query string or HTML attributes.',
  `module` varchar(255) NOT NULL DEFAULT 'system' COMMENT 'The name of the module that generated this link.',
  `hidden` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag for whether the link should be rendered in menus. (1 = a disabled menu item that may be shown on admin screens, -1 = a menu callback, 0 = a normal, visible link)',
  `external` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate if the link points to a full URL starting with a protocol, like http:// (1 = external, 0 = internal).',
  `has_children` smallint(6) NOT NULL DEFAULT '0' COMMENT 'Flag indicating whether any links have this link as a parent (1 = children exist, 0 = no children).',
  `expanded` smallint(6) NOT NULL DEFAULT '0' COMMENT 'Flag for whether this link should be rendered as expanded in menus - expanded links always have their child links displayed, instead of only when the link is in the active trail (1 = expanded, 0 = not expanded)',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'Link weight among links in the same menu at the same depth.',
  `depth` smallint(6) NOT NULL DEFAULT '0' COMMENT 'The depth relative to the top level. A link with plid == 0 will have depth == 1.',
  `customized` smallint(6) NOT NULL DEFAULT '0' COMMENT 'A flag to indicate that the user has manually created or edited the link (1 = customized, 0 = not customized).',
  `p1` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The first mlid in the materialized path. If N = depth, then pN must equal the mlid. If depth > 1 then p(N-1) must equal the plid. All pX where X > depth must equal zero. The columns p1 .. p9 are also called the parents.',
  `p2` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The second mlid in the materialized path. See p1.',
  `p3` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The third mlid in the materialized path. See p1.',
  `p4` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The fourth mlid in the materialized path. See p1.',
  `p5` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The fifth mlid in the materialized path. See p1.',
  `p6` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The sixth mlid in the materialized path. See p1.',
  `p7` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The seventh mlid in the materialized path. See p1.',
  `p8` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The eighth mlid in the materialized path. See p1.',
  `p9` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The ninth mlid in the materialized path. See p1.',
  `updated` smallint(6) NOT NULL DEFAULT '0' COMMENT 'Flag that indicates that this link was generated during the update from Drupal 5.',
  PRIMARY KEY (`mlid`),
  KEY `path_menu` (`link_path`(128),`menu_name`),
  KEY `menu_plid_expand_child` (`menu_name`,`plid`,`expanded`,`has_children`),
  KEY `menu_parents` (`menu_name`,`p1`,`p2`,`p3`,`p4`,`p5`,`p6`,`p7`,`p8`,`p9`),
  KEY `router_path` (`router_path`(128))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Contains the individual links within a menu.';



# Dump of table menu_router
# ------------------------------------------------------------

CREATE TABLE `menu_router` (
  `path` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: the Drupal path this entry describes',
  `load_functions` blob NOT NULL COMMENT 'A serialized array of function names (like node_load) to be called to load an object corresponding to a part of the current path.',
  `to_arg_functions` blob NOT NULL COMMENT 'A serialized array of function names (like user_uid_optional_to_arg) to be called to replace a part of the router path with another string.',
  `access_callback` varchar(255) NOT NULL DEFAULT '' COMMENT 'The callback which determines the access to this router path. Defaults to user_access.',
  `access_arguments` blob COMMENT 'A serialized array of arguments for the access callback.',
  `page_callback` varchar(255) NOT NULL DEFAULT '' COMMENT 'The name of the function that renders the page.',
  `page_arguments` blob COMMENT 'A serialized array of arguments for the page callback.',
  `delivery_callback` varchar(255) NOT NULL DEFAULT '' COMMENT 'The name of the function that sends the result of the page_callback function to the browser.',
  `fit` int(11) NOT NULL DEFAULT '0' COMMENT 'A numeric representation of how specific the path is.',
  `number_parts` smallint(6) NOT NULL DEFAULT '0' COMMENT 'Number of parts in this router path.',
  `context` int(11) NOT NULL DEFAULT '0' COMMENT 'Only for local tasks (tabs) - the context of a local task to control its placement.',
  `tab_parent` varchar(255) NOT NULL DEFAULT '' COMMENT 'Only for local tasks (tabs) - the router path of the parent page (which may also be a local task).',
  `tab_root` varchar(255) NOT NULL DEFAULT '' COMMENT 'Router path of the closest non-tab parent page. For pages that are not local tasks, this will be the same as the path.',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT 'The title for the current page, or the title for the tab if this is a local task.',
  `title_callback` varchar(255) NOT NULL DEFAULT '' COMMENT 'A function which will alter the title. Defaults to t()',
  `title_arguments` varchar(255) NOT NULL DEFAULT '' COMMENT 'A serialized array of arguments for the title callback. If empty, the title will be used as the sole argument for the title callback.',
  `theme_callback` varchar(255) NOT NULL DEFAULT '' COMMENT 'A function which returns the name of the theme that will be used to render this page. If left empty, the default theme will be used.',
  `theme_arguments` varchar(255) NOT NULL DEFAULT '' COMMENT 'A serialized array of arguments for the theme callback.',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT 'Numeric representation of the type of the menu item, like MENU_LOCAL_TASK.',
  `description` text NOT NULL COMMENT 'A description of this item.',
  `position` varchar(255) NOT NULL DEFAULT '' COMMENT 'The position of the block (left or right) on the system administration page for this item.',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'Weight of the element. Lighter weights are higher up, heavier weights go down.',
  `include_file` mediumtext COMMENT 'The file to include for this element, usually the page callback function lives in this file.',
  PRIMARY KEY (`path`),
  KEY `fit` (`fit`),
  KEY `tab_parent` (`tab_parent`(64),`weight`,`title`),
  KEY `tab_root_weight_title` (`tab_root`(64),`weight`,`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Maps paths to various callbacks (access, page and title)';



# Dump of table node
# ------------------------------------------------------------

CREATE TABLE `node` (
  `nid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The primary identifier for a node.',
  `vid` int(10) unsigned DEFAULT NULL COMMENT 'The current node_revision.vid version identifier.',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT 'The node_type.type of this node.',
  `language` varchar(12) NOT NULL DEFAULT '' COMMENT 'The languages.language of this node.',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT 'The title of this node, always treated as non-markup plain text.',
  `uid` int(11) NOT NULL DEFAULT '0' COMMENT 'The users.uid that owns this node; initially, this is the user that created it.',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT 'Boolean indicating whether the node is published (visible to non-administrators).',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'The Unix timestamp when the node was created.',
  `changed` int(11) NOT NULL DEFAULT '0' COMMENT 'The Unix timestamp when the node was most recently saved.',
  `comment` int(11) NOT NULL DEFAULT '0' COMMENT 'Whether comments are allowed on this node: 0 = no, 1 = closed (read only), 2 = open (read/write).',
  `promote` int(11) NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether the node should be displayed on the front page.',
  `sticky` int(11) NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether the node should be displayed at the top of lists in which it appears.',
  `tnid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The translation set id for this node, which equals the node id of the source post in each set.',
  `translate` int(11) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether this translation page needs to be updated.',
  PRIMARY KEY (`nid`),
  UNIQUE KEY `vid` (`vid`),
  KEY `node_changed` (`changed`),
  KEY `node_created` (`created`),
  KEY `node_frontpage` (`promote`,`status`,`sticky`,`created`),
  KEY `node_status_type` (`status`,`type`,`nid`),
  KEY `node_title_type` (`title`,`type`(4)),
  KEY `node_type` (`type`(4)),
  KEY `uid` (`uid`),
  KEY `tnid` (`tnid`),
  KEY `translate` (`translate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='The base table for nodes.';



# Dump of table node_access
# ------------------------------------------------------------

CREATE TABLE `node_access` (
  `nid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The node.nid this record affects.',
  `gid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The grant ID a user must possess in the specified realm to gain this rowâ€™s privileges on the node.',
  `realm` varchar(255) NOT NULL DEFAULT '' COMMENT 'The realm in which the user must possess the grant ID. Each node access node can define one or more realms.',
  `grant_view` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether a user with the realm/grant pair can view this node.',
  `grant_update` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether a user with the realm/grant pair can edit this node.',
  `grant_delete` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether a user with the realm/grant pair can delete this node.',
  PRIMARY KEY (`nid`,`gid`,`realm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Identifies which realm/grant pairs a user must possess in...';



# Dump of table node_comment_statistics
# ------------------------------------------------------------

CREATE TABLE `node_comment_statistics` (
  `nid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The node.nid for which the statistics are compiled.',
  `cid` int(11) NOT NULL DEFAULT '0' COMMENT 'The comment.cid of the last comment.',
  `last_comment_timestamp` int(11) NOT NULL DEFAULT '0' COMMENT 'The Unix timestamp of the last comment that was posted within this node, from comment.changed.',
  `last_comment_name` varchar(60) DEFAULT NULL COMMENT 'The name of the latest author to post a comment on this node, from comment.name.',
  `last_comment_uid` int(11) NOT NULL DEFAULT '0' COMMENT 'The user ID of the latest author to post a comment on this node, from comment.uid.',
  `comment_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The total number of comments on this node.',
  PRIMARY KEY (`nid`),
  KEY `node_comment_timestamp` (`last_comment_timestamp`),
  KEY `comment_count` (`comment_count`),
  KEY `last_comment_uid` (`last_comment_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Maintains statistics of node and comments posts to show ...';



# Dump of table node_revision
# ------------------------------------------------------------

CREATE TABLE `node_revision` (
  `nid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The node this version belongs to.',
  `vid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The primary identifier for this version.',
  `uid` int(11) NOT NULL DEFAULT '0' COMMENT 'The users.uid that created this version.',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT 'The title of this version.',
  `log` longtext NOT NULL COMMENT 'The log entry explaining the changes in this version.',
  `timestamp` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when this version was created.',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT 'Boolean indicating whether the node (at the time of this revision) is published (visible to non-administrators).',
  `comment` int(11) NOT NULL DEFAULT '0' COMMENT 'Whether comments are allowed on this node (at the time of this revision): 0 = no, 1 = closed (read only), 2 = open (read/write).',
  `promote` int(11) NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether the node (at the time of this revision) should be displayed on the front page.',
  `sticky` int(11) NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether the node (at the time of this revision) should be displayed at the top of lists in which it appears.',
  PRIMARY KEY (`vid`),
  KEY `nid` (`nid`),
  KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores information about each saved version of a node.';



# Dump of table node_type
# ------------------------------------------------------------

CREATE TABLE `node_type` (
  `type` varchar(32) NOT NULL COMMENT 'The machine-readable name of this type.',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'The human-readable name of this type.',
  `base` varchar(255) NOT NULL COMMENT 'The base string used to construct callbacks corresponding to this node type.',
  `module` varchar(255) NOT NULL COMMENT 'The module defining this node type.',
  `description` mediumtext NOT NULL COMMENT 'A brief description of this type.',
  `help` mediumtext NOT NULL COMMENT 'Help information shown to the user when creating a node of this type.',
  `has_title` tinyint(3) unsigned NOT NULL COMMENT 'Boolean indicating whether this type uses the node.title field.',
  `title_label` varchar(255) NOT NULL DEFAULT '' COMMENT 'The label displayed for the title field on the edit form.',
  `custom` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether this type is defined by a module (FALSE) or by a user via Add content type (TRUE).',
  `modified` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether this type has been modified by an administrator; currently not used in any way.',
  `locked` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether the administrator can change the machine name of this type.',
  `disabled` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'A boolean indicating whether the node type is disabled.',
  `orig_type` varchar(255) NOT NULL DEFAULT '' COMMENT 'The original machine-readable name of this node type. This may be different from the current type name if the locked field is 0.',
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores information about all defined node types.';



# Dump of table page_manager_handlers
# ------------------------------------------------------------

CREATE TABLE `page_manager_handlers` (
  `did` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary ID field for the table. Not used for anything except internal lookups.',
  `name` varchar(255) DEFAULT NULL COMMENT 'Unique ID for this task handler. Used to identify it programmatically.',
  `task` varchar(64) DEFAULT NULL COMMENT 'ID of the task this handler is for.',
  `subtask` varchar(64) NOT NULL DEFAULT '' COMMENT 'ID of the subtask this handler is for.',
  `handler` varchar(64) DEFAULT NULL COMMENT 'ID of the task handler being used.',
  `weight` int(11) DEFAULT NULL COMMENT 'The order in which this handler appears. Lower numbers go first.',
  `conf` longtext NOT NULL COMMENT 'Serialized configuration of the handler, if needed.',
  PRIMARY KEY (`did`),
  UNIQUE KEY `name` (`name`),
  KEY `fulltask` (`task`,`subtask`,`weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table page_manager_pages
# ------------------------------------------------------------

CREATE TABLE `page_manager_pages` (
  `pid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary ID field for the table. Not used for anything except internal lookups.',
  `name` varchar(255) DEFAULT NULL COMMENT 'Unique ID for this subtask. Used to identify it programmatically.',
  `task` varchar(64) DEFAULT 'page' COMMENT 'What type of page this is, so that we can use the same mechanism for creating tighter UIs for targeted pages.',
  `admin_title` varchar(255) DEFAULT NULL COMMENT 'Human readable title for this page subtask.',
  `admin_description` longtext COMMENT 'Administrative description of this item.',
  `path` varchar(255) DEFAULT NULL COMMENT 'The menu path that will invoke this task.',
  `access` longtext NOT NULL COMMENT 'Access configuration for this path.',
  `menu` longtext NOT NULL COMMENT 'Serialized configuration of Drupal menu visibility settings for this item.',
  `arguments` longtext NOT NULL COMMENT 'Configuration of arguments for this menu item.',
  `conf` longtext NOT NULL COMMENT 'Serialized configuration of the page, if needed.',
  PRIMARY KEY (`pid`),
  UNIQUE KEY `name` (`name`),
  KEY `task` (`task`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Contains page subtasks for implementing pages with...';



# Dump of table page_manager_weights
# ------------------------------------------------------------

CREATE TABLE `page_manager_weights` (
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'Unique ID for this task handler. Used to identify it programmatically.',
  `weight` int(11) DEFAULT NULL COMMENT 'The order in which this handler appears. Lower numbers go first.',
  PRIMARY KEY (`name`),
  KEY `weights` (`name`,`weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Contains override weights for page_manager handlers that...';



# Dump of table queue
# ------------------------------------------------------------

CREATE TABLE `queue` (
  `item_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key: Unique item ID.',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'The queue name.',
  `data` longblob COMMENT 'The arbitrary data for the item.',
  `expire` int(11) NOT NULL DEFAULT '0' COMMENT 'Timestamp when the claim lease expires on the item.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'Timestamp when the item was created.',
  PRIMARY KEY (`item_id`),
  KEY `name_created` (`name`,`created`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores items in queues.';



# Dump of table rdf_mapping
# ------------------------------------------------------------

CREATE TABLE `rdf_mapping` (
  `type` varchar(128) NOT NULL COMMENT 'The name of the entity type a mapping applies to (node, user, comment, etc.).',
  `bundle` varchar(128) NOT NULL COMMENT 'The name of the bundle a mapping applies to.',
  `mapping` longblob COMMENT 'The serialized mapping of the bundle type and fields to RDF terms.',
  PRIMARY KEY (`type`,`bundle`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores custom RDF mappings for user defined content types...';



# Dump of table registry
# ------------------------------------------------------------

CREATE TABLE `registry` (
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'The name of the function, class, or interface.',
  `type` varchar(9) NOT NULL DEFAULT '' COMMENT 'Either function or class or interface.',
  `filename` varchar(255) NOT NULL COMMENT 'Name of the file.',
  `module` varchar(255) NOT NULL DEFAULT '' COMMENT 'Name of the module the file belongs to.',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'The order in which this moduleâ€™s hooks should be invoked relative to other modules. Equal-weighted modules are ordered by name.',
  PRIMARY KEY (`name`,`type`),
  KEY `hook` (`type`,`weight`,`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Each record is a function, class, or interface name and...';



# Dump of table registry_file
# ------------------------------------------------------------

CREATE TABLE `registry_file` (
  `filename` varchar(255) NOT NULL COMMENT 'Path to the file.',
  `hash` varchar(64) NOT NULL COMMENT 'sha-256 hash of the fileâ€™s contents when last parsed.',
  PRIMARY KEY (`filename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Files parsed to build the registry.';



# Dump of table role
# ------------------------------------------------------------

CREATE TABLE `role` (
  `rid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key: Unique role ID.',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT 'Unique role name.',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'The weight of this role in listings and the user interface.',
  PRIMARY KEY (`rid`),
  UNIQUE KEY `name` (`name`),
  KEY `name_weight` (`name`,`weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores user roles.';



# Dump of table role_permission
# ------------------------------------------------------------

CREATE TABLE `role_permission` (
  `rid` int(10) unsigned NOT NULL COMMENT 'Foreign Key: role.rid.',
  `permission` varchar(128) NOT NULL DEFAULT '' COMMENT 'A single permission granted to the role identified by rid.',
  `module` varchar(255) NOT NULL DEFAULT '' COMMENT 'The module declaring the permission.',
  PRIMARY KEY (`rid`,`permission`),
  KEY `permission` (`permission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores the permissions assigned to user roles.';



# Dump of table search_dataset
# ------------------------------------------------------------

CREATE TABLE `search_dataset` (
  `sid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Search item ID, e.g. node ID for nodes.',
  `type` varchar(16) NOT NULL COMMENT 'Type of item, e.g. node.',
  `data` longtext NOT NULL COMMENT 'List of space-separated words from the item.',
  `reindex` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Set to force node reindexing.',
  PRIMARY KEY (`sid`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores items that will be searched.';



# Dump of table search_index
# ------------------------------------------------------------

CREATE TABLE `search_index` (
  `word` varchar(50) NOT NULL DEFAULT '' COMMENT 'The search_total.word that is associated with the search item.',
  `sid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The search_dataset.sid of the searchable item to which the word belongs.',
  `type` varchar(16) NOT NULL COMMENT 'The search_dataset.type of the searchable item to which the word belongs.',
  `score` float DEFAULT NULL COMMENT 'The numeric score of the word, higher being more important.',
  PRIMARY KEY (`word`,`sid`,`type`),
  KEY `sid_type` (`sid`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores the search index, associating words, items and...';



# Dump of table search_node_links
# ------------------------------------------------------------

CREATE TABLE `search_node_links` (
  `sid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The search_dataset.sid of the searchable item containing the link to the node.',
  `type` varchar(16) NOT NULL DEFAULT '' COMMENT 'The search_dataset.type of the searchable item containing the link to the node.',
  `nid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The node.nid that this item links to.',
  `caption` longtext COMMENT 'The text used to link to the node.nid.',
  PRIMARY KEY (`sid`,`type`,`nid`),
  KEY `nid` (`nid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores items (like nodes) that link to other nodes, used...';



# Dump of table search_total
# ------------------------------------------------------------

CREATE TABLE `search_total` (
  `word` varchar(50) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique word in the search index.',
  `count` float DEFAULT NULL COMMENT 'The count of the word in the index using Zipfâ€™s law to equalize the probability distribution.',
  PRIMARY KEY (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores search totals for words.';



# Dump of table semaphore
# ------------------------------------------------------------

CREATE TABLE `semaphore` (
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Unique name.',
  `value` varchar(255) NOT NULL DEFAULT '' COMMENT 'A value for the semaphore.',
  `expire` double NOT NULL COMMENT 'A Unix timestamp with microseconds indicating when the semaphore should expire.',
  PRIMARY KEY (`name`),
  KEY `value` (`value`),
  KEY `expire` (`expire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table for holding semaphores, locks, flags, etc. that...';



# Dump of table sequences
# ------------------------------------------------------------

CREATE TABLE `sequences` (
  `value` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The value of the sequence.',
  PRIMARY KEY (`value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores IDs.';



# Dump of table sessions
# ------------------------------------------------------------

CREATE TABLE `sessions` (
  `uid` int(10) unsigned NOT NULL COMMENT 'The users.uid corresponding to a session, or 0 for anonymous user.',
  `sid` varchar(128) NOT NULL COMMENT 'A session ID. The value is generated by Drupalâ€™s session handlers.',
  `ssid` varchar(128) NOT NULL DEFAULT '' COMMENT 'Secure session ID. The value is generated by Drupalâ€™s session handlers.',
  `hostname` varchar(128) NOT NULL DEFAULT '' COMMENT 'The IP address that last used this session ID (sid).',
  `timestamp` int(11) NOT NULL DEFAULT '0' COMMENT 'The Unix timestamp when this session last requested a page. Old records are purged by PHP automatically.',
  `cache` int(11) NOT NULL DEFAULT '0' COMMENT 'The time of this userâ€™s last post. This is used when the site has specified a minimum_cache_lifetime. See cache_get().',
  `session` longblob COMMENT 'The serialized contents of $_SESSION, an array of name/value pairs that persists across page requests by this session ID. Drupal loads $_SESSION from here at the start of each request and saves it at the end.',
  PRIMARY KEY (`sid`,`ssid`),
  KEY `timestamp` (`timestamp`),
  KEY `uid` (`uid`),
  KEY `ssid` (`ssid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Drupalâ€™s session handlers read and write into the...';



# Dump of table shortcut_set
# ------------------------------------------------------------

CREATE TABLE `shortcut_set` (
  `set_name` varchar(32) NOT NULL DEFAULT '' COMMENT 'Primary Key: The menu_links.menu_name under which the setâ€™s links are stored.',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT 'The title of the set.',
  PRIMARY KEY (`set_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores information about sets of shortcuts links.';



# Dump of table shortcut_set_users
# ------------------------------------------------------------

CREATE TABLE `shortcut_set_users` (
  `uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The users.uid for this set.',
  `set_name` varchar(32) NOT NULL DEFAULT '' COMMENT 'The shortcut_set.set_name that will be displayed for this user.',
  PRIMARY KEY (`uid`),
  KEY `set_name` (`set_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Maps users to shortcut sets.';



# Dump of table stylizer
# ------------------------------------------------------------

CREATE TABLE `stylizer` (
  `sid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT 'Unique ID for this style. Used to identify it programmatically.',
  `admin_title` varchar(255) DEFAULT NULL COMMENT 'Human readable title for this style.',
  `admin_description` longtext COMMENT 'Administrative description of this style.',
  `settings` longtext COMMENT 'A serialized array of settings specific to the style base that describes this plugin.',
  PRIMARY KEY (`sid`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Customized stylizer styles created by administrative users.';



# Dump of table system
# ------------------------------------------------------------

CREATE TABLE `system` (
  `filename` varchar(255) NOT NULL DEFAULT '' COMMENT 'The path of the primary file for this item, relative to the Drupal root; e.g. modules/node/node.module.',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'The name of the item; e.g. node.',
  `type` varchar(12) NOT NULL DEFAULT '' COMMENT 'The type of the item, either module, theme, or theme_engine.',
  `owner` varchar(255) NOT NULL DEFAULT '' COMMENT 'A themeâ€™s â€™parentâ€™ . Can be either a theme or an engine.',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether or not this item is enabled.',
  `bootstrap` int(11) NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether this module is loaded during Drupalâ€™s early bootstrapping phase (e.g. even before the page cache is consulted).',
  `schema_version` smallint(6) NOT NULL DEFAULT '-1' COMMENT 'The moduleâ€™s database schema version number. -1 if the module is not installed (its tables do not exist); 0 or the largest N of the moduleâ€™s hook_update_N() function that has either been run or existed when the module was first installed.',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'The order in which this moduleâ€™s hooks should be invoked relative to other modules. Equal-weighted modules are ordered by name.',
  `info` blob COMMENT 'A serialized array containing information from the moduleâ€™s .info file; keys can include name, description, package, version, core, dependencies, and php.',
  PRIMARY KEY (`filename`),
  KEY `system_list` (`status`,`bootstrap`,`type`,`weight`,`name`),
  KEY `type_name` (`type`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='A list of all modules, themes, and theme engines that are...';



# Dump of table taxonomy_index
# ------------------------------------------------------------

CREATE TABLE `taxonomy_index` (
  `nid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The node.nid this record tracks.',
  `tid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The term ID.',
  `sticky` tinyint(4) DEFAULT '0' COMMENT 'Boolean indicating whether the node is sticky.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'The Unix timestamp when the node was created.',
  KEY `term_node` (`tid`,`sticky`,`created`),
  KEY `nid` (`nid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Maintains denormalized information about node/term...';



# Dump of table taxonomy_term_data
# ------------------------------------------------------------

CREATE TABLE `taxonomy_term_data` (
  `tid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key: Unique term ID.',
  `vid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The taxonomy_vocabulary.vid of the vocabulary to which the term is assigned.',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'The term name.',
  `description` longtext COMMENT 'A description of the term.',
  `format` varchar(255) DEFAULT NULL COMMENT 'The filter_format.format of the description.',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'The weight of this term in relation to other terms.',
  PRIMARY KEY (`tid`),
  KEY `taxonomy_tree` (`vid`,`weight`,`name`),
  KEY `vid_name` (`vid`,`name`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores term information.';



# Dump of table taxonomy_term_hierarchy
# ------------------------------------------------------------

CREATE TABLE `taxonomy_term_hierarchy` (
  `tid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Primary Key: The taxonomy_term_data.tid of the term.',
  `parent` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Primary Key: The taxonomy_term_data.tid of the termâ€™s parent. 0 indicates no parent.',
  PRIMARY KEY (`tid`,`parent`),
  KEY `parent` (`parent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores the hierarchical relationship between terms.';



# Dump of table taxonomy_vocabulary
# ------------------------------------------------------------

CREATE TABLE `taxonomy_vocabulary` (
  `vid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key: Unique vocabulary ID.',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'Name of the vocabulary.',
  `machine_name` varchar(255) NOT NULL DEFAULT '' COMMENT 'The vocabulary machine name.',
  `description` longtext COMMENT 'Description of the vocabulary.',
  `hierarchy` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'The type of hierarchy allowed within the vocabulary. (0 = disabled, 1 = single, 2 = multiple)',
  `module` varchar(255) NOT NULL DEFAULT '' COMMENT 'The module which created the vocabulary.',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'The weight of this vocabulary in relation to other vocabularies.',
  PRIMARY KEY (`vid`),
  UNIQUE KEY `machine_name` (`machine_name`),
  KEY `list` (`weight`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores vocabulary information.';



# Dump of table trigger_assignments
# ------------------------------------------------------------

CREATE TABLE `trigger_assignments` (
  `hook` varchar(78) NOT NULL DEFAULT '' COMMENT 'Primary Key: The name of the internal Drupal hook; for example, node_insert.',
  `aid` varchar(255) NOT NULL DEFAULT '' COMMENT 'Primary Key: Actionâ€™s actions.aid.',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT 'The weight of the trigger assignment in relation to other triggers.',
  PRIMARY KEY (`hook`,`aid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Maps trigger to hook and operation assignments from...';



# Dump of table url_alias
# ------------------------------------------------------------

CREATE TABLE `url_alias` (
  `pid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'A unique path alias identifier.',
  `source` varchar(255) NOT NULL DEFAULT '' COMMENT 'The Drupal path this alias is for; e.g. node/12.',
  `alias` varchar(255) NOT NULL DEFAULT '' COMMENT 'The alias for this path; e.g. title-of-the-story.',
  `language` varchar(12) NOT NULL DEFAULT '' COMMENT 'The language this alias is for; if â€™undâ€™, the alias will be used for unknown languages. Each Drupal path can have an alias for each supported language.',
  PRIMARY KEY (`pid`),
  KEY `alias_language_pid` (`alias`,`language`,`pid`),
  KEY `source_language_pid` (`source`,`language`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='A list of URL aliases for Drupal paths; a user may visit...';



# Dump of table users
# ------------------------------------------------------------

CREATE TABLE `users` (
  `uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Primary Key: Unique user ID.',
  `name` varchar(60) NOT NULL DEFAULT '' COMMENT 'Unique user name.',
  `pass` varchar(128) NOT NULL DEFAULT '' COMMENT 'Userâ€™s password (hashed).',
  `mail` varchar(254) DEFAULT '' COMMENT 'Userâ€™s e-mail address.',
  `theme` varchar(255) NOT NULL DEFAULT '' COMMENT 'Userâ€™s default theme.',
  `signature` varchar(255) NOT NULL DEFAULT '' COMMENT 'Userâ€™s signature.',
  `signature_format` varchar(255) DEFAULT NULL COMMENT 'The filter_format.format of the signature.',
  `created` int(11) NOT NULL DEFAULT '0' COMMENT 'Timestamp for when user was created.',
  `access` int(11) NOT NULL DEFAULT '0' COMMENT 'Timestamp for previous time user accessed the site.',
  `login` int(11) NOT NULL DEFAULT '0' COMMENT 'Timestamp for userâ€™s last login.',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Whether the user is active(1) or blocked(0).',
  `timezone` varchar(32) DEFAULT NULL COMMENT 'Userâ€™s time zone.',
  `language` varchar(12) NOT NULL DEFAULT '' COMMENT 'Userâ€™s default language.',
  `picture` int(11) NOT NULL DEFAULT '0' COMMENT 'Foreign key: file_managed.fid of userâ€™s picture.',
  `init` varchar(254) DEFAULT '' COMMENT 'E-mail address used for initial account creation.',
  `data` longblob COMMENT 'A serialized array of name value pairs that are related to the user. Any form values posted during user edit are stored and are loaded into the $user object during user_load(). Use of this field is discouraged and it will likely disappear in a future...',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `name` (`name`),
  KEY `access` (`access`),
  KEY `created` (`created`),
  KEY `mail` (`mail`),
  KEY `picture` (`picture`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores user data.';



# Dump of table users_roles
# ------------------------------------------------------------

CREATE TABLE `users_roles` (
  `uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Primary Key: users.uid for user.',
  `rid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Primary Key: role.rid for role.',
  PRIMARY KEY (`uid`,`rid`),
  KEY `rid` (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Maps users to roles.';



# Dump of table variable
# ------------------------------------------------------------

CREATE TABLE `variable` (
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT 'The name of the variable.',
  `value` longblob NOT NULL COMMENT 'The value of the variable.',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Named variable/value pairs created by Drupal core or any...';



# Dump of table views_display
# ------------------------------------------------------------

CREATE TABLE `views_display` (
  `vid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The view this display is attached to.',
  `id` varchar(64) NOT NULL DEFAULT '' COMMENT 'An identifier for this display; usually generated from the display_plugin, so should be something like page or page_1 or block_2, etc.',
  `display_title` varchar(64) NOT NULL DEFAULT '' COMMENT 'The title of the display, viewable by the administrator.',
  `display_plugin` varchar(64) NOT NULL DEFAULT '' COMMENT 'The type of the display. Usually page, block or embed, but is pluggable so may be other things.',
  `position` int(11) DEFAULT '0' COMMENT 'The order in which this display is loaded.',
  `display_options` longtext COMMENT 'A serialized array of options for this display; it contains options that are generally only pertinent to that display plugin type.',
  PRIMARY KEY (`vid`,`id`),
  KEY `vid` (`vid`,`position`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores information about each display attached to a view.';



# Dump of table views_view
# ------------------------------------------------------------

CREATE TABLE `views_view` (
  `vid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The view ID of the field, defined by the database.',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT 'The unique name of the view. This is the primary field views are loaded from, and is used so that views may be internal and not necessarily in the database. May only be alphanumeric characters plus underscores.',
  `description` varchar(255) DEFAULT '' COMMENT 'A description of the view for the admin interface.',
  `tag` varchar(255) DEFAULT '' COMMENT 'A tag used to group/sort views in the admin interface',
  `base_table` varchar(64) NOT NULL DEFAULT '' COMMENT 'What table this view is based on, such as node, user, comment, or term.',
  `human_name` varchar(255) DEFAULT '' COMMENT 'A human readable name used to be displayed in the admin interface',
  `core` int(11) DEFAULT '0' COMMENT 'Stores the drupal core version of the view.',
  PRIMARY KEY (`vid`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores the general data for a view.';



# Dump of table watchdog
# ------------------------------------------------------------

CREATE TABLE `watchdog` (
  `wid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key: Unique watchdog event ID.',
  `uid` int(11) NOT NULL DEFAULT '0' COMMENT 'The users.uid of the user who triggered the event.',
  `type` varchar(64) NOT NULL DEFAULT '' COMMENT 'Type of log message, for example "user" or "page not found."',
  `message` longtext NOT NULL COMMENT 'Text of log message to be passed into the t() function.',
  `variables` longblob NOT NULL COMMENT 'Serialized array of variables that match the message string and that is passed into the t() function.',
  `severity` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'The severity level of the event; ranges from 0 (Emergency) to 7 (Debug)',
  `link` varchar(255) DEFAULT '' COMMENT 'Link to view the result of the event.',
  `location` text NOT NULL COMMENT 'URL of the origin of the event.',
  `referer` text COMMENT 'URL of referring page.',
  `hostname` varchar(128) NOT NULL DEFAULT '' COMMENT 'Hostname of the user who triggered the event.',
  `timestamp` int(11) NOT NULL DEFAULT '0' COMMENT 'Unix timestamp of when event occurred.',
  PRIMARY KEY (`wid`),
  KEY `type` (`type`),
  KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table that contains logs of all system events.';



# Dump of table wysiwyg
# ------------------------------------------------------------

CREATE TABLE `wysiwyg` (
  `format` varchar(255) NOT NULL COMMENT 'The filter_format.format of the text format.',
  `editor` varchar(128) NOT NULL DEFAULT '' COMMENT 'Internal name of the editor attached to the text format.',
  `settings` text COMMENT 'Configuration settings for the editor.',
  PRIMARY KEY (`format`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores Wysiwyg profiles.';



# Dump of table wysiwyg_user
# ------------------------------------------------------------

CREATE TABLE `wysiwyg_user` (
  `uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The users.uid of the user.',
  `format` varchar(255) DEFAULT NULL COMMENT 'The filter_format.format of the text format.',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether the format is enabled by default.',
  KEY `uid` (`uid`),
  KEY `format` (`format`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stores user preferences for wysiwyg profiles.';




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
