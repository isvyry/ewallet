package ua.svyry.ewallet.quartz;

import org.quartz.JobDetail;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class QuartzConfig {

    @Bean
    public JobDetailFactoryBean unblockJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(CustomerEnablingJob.class);
        // job has to be durable to be stored in DB:
        factoryBean.setDescription("That job sets isBlockedForTransactions value to 'true' for all blocked customers " +
                "every working day at 1:00 AM UTC");
        factoryBean.setDurability(true);
        return factoryBean;
    }

    @Bean
    public CronTriggerFactoryBean cronTriggerFactoryBean(JobDetail jobDetail) {
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setJobDetail(jobDetail);
        bean.setName("EveryWorkingDayTrigger");
        bean.setCronExpression("0 0 1 ? * 2-6");
        return bean;
    }
}

