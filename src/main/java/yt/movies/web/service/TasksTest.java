package yt.movies.web.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import yt.movies.web.App;

/**
 * Created by justas.rutkauskas on 4/21/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TasksTest {

    @Autowired
    private Tasks tasks;

    @Test
    public void testDownloadMovies()
    {
        tasks.downloadMovies();
    }
}
