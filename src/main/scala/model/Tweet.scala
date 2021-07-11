package model

class Tweet() {
  private[model] var text = ""

  def this(text: String) {
    this()
    this.text = text
  }

  def getText: String = text
}
