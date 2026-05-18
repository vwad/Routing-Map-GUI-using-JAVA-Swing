package SQLDatabase.Config;

import org.sqlite.SQLiteConfig;
public class SQLiteBaseConfigFactory implements SQLiteConfigFactory{

    @Override
    public SQLiteConfig getConfig() {
        SQLiteConfig config = new SQLiteConfig();
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);
        config.setJournalSizeLimit(6144000);
        config.setCacheSize(20000);
        config.setPageSize(4096);
        config.setTempStore(SQLiteConfig.TempStore.MEMORY);
        return config;
    }
}
