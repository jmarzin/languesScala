package models

import java.util.Date

import org.squeryl.KeyedEntity
/**
 * Created by jacques on 23/02/15.
 */
case class FormType (
                    id: Long,
                    number: Integer,
                    language_id: String,
                    in_french: String,
                    last_update: Date
                    ) extends KeyedEntity[Long]
