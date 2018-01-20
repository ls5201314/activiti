package service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring-core.xml",
        "classpath:spring-mybatis.xml",
        "classpath:activiti.context.xml"})
public class BaseService {

   protected final Log logger = LogFactory.getLog(getClass());
}
