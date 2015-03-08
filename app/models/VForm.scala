package models

import java.util.Date

import org.squeryl.KeyedEntity
/**
 * Created by jacques on 23/02/15.
 */
case class VForm(
               id: Long,
               language_id: String,
               verb_id: Long,
               form_type_id: Long,
               in_french: String,
               inLanguage: String,
               pronunciation: String,
               last_update: Date
               ) extends KeyedEntity[Long]
