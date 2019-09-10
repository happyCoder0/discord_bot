import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

object Treater {
  def main(args: Array[String]): Unit = {

  }
  def treat(event: GuildMessageReceivedEvent): Unit = {
    if(event.getGuild.getSelfMember.hasPermission(Permission.MESSAGE_WRITE) & event.getChannel.canTalk() & event.getMessage.getContentRaw.toLowerCase == "say hi"){
      event.getChannel.sendMessage("hello from Scala module! :)").queue()
    }
  }
}
