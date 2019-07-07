appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d %level %logger - %msg%n"
    }
}

root(INFO, ["STDOUT"])

logger("slick.basic.BasicBackend.action", DEBUG)

logger("slick.jdbc.JdbcBackend.statement", DEBUG)

logger("com.avs.TestCode1", DEBUG)

logger("TestCode", DEBUG)