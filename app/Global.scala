import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.{Session, SessionFactory}
import play.api.db.DB
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {
  val admin = false
  override def onStart(app: Application) {
    SessionFactory.concreteFactory = Some( () =>
      Session.create(DB.getConnection()(app), new PostgreSqlAdapter)
    )
  }
}