package logic.usecase.audit.utils

const val ADDED_ACTION = "Added"
const val UPDATED_ACTION = "Updated"
const val DELETED_ACTION = "Deleted"
const val TASK_MESSAGE_TEMPLATE = "%s: %s '%s' (ID: %s) %s"
const val PROJECT_MESSAGE_TEMPLATE = "%s: %s '%s' (ID: %s) %s"
const val STATE_MESSAGE_TEMPLATE = "%s: %s '%s' (ID: %s)"
const val TASK_PROJECT_SUFFIX = "to the '%s' project"
const val PROJECT_STATE_SUFFIX = "with state '%s'"
const val COMBINED_DETAIL_FORMAT = "%s. %s."
const val NAME_UPDATE = "Name from '%s' to '%s'"
const val DESCRIPTION_UPDATE = "Description: '%s'"
const val STATE_UPDATED = "State: '%s'"
