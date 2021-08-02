package com.nisum.myteam.configuration;

import java.io.IOException;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.nisum.myteam.schedular.AutowiringSpringBeanJobFactory;
import com.nisum.myteam.schedular.MyTeamCronSchedularJob;

@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class SchedulerConfig {

	@Value("${cron.expression}")
	private String cronExp;

	@Bean
	public JobFactory jobFactory(ApplicationContext applicationContext) {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean
	public Scheduler schedulerFactoryBean(JobFactory jobFactory,
			@Qualifier("myTimeJobTrigger") Trigger myTeamJobTrigger) throws Exception {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setOverwriteExistingJobs(true);
		factory.setJobFactory(jobFactory);

		factory.setQuartzProperties(quartzProperties());
		factory.afterPropertiesSet();

		Scheduler scheduler = factory.getScheduler();
		scheduler.setJobFactory(jobFactory);
		scheduler.scheduleJob((JobDetail) myTeamJobTrigger.getJobDataMap().get("jobDetail"), myTeamJobTrigger);

		scheduler.start();
		return scheduler;
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	@Bean
	public JobDetailFactoryBean myTeamJobDetail() {
		return createJobDetail(MyTeamCronSchedularJob.class);
	}

	@Bean(name = "myTimeJobTrigger")
	public CronTriggerFactoryBean myTimeJobTrigger(@Qualifier("myTeamJobDetail") JobDetail jobDetail,
			@Value("${myTeamjob.frequency}") long frequency) {
		return createCronTrigger(jobDetail, frequency);
	}

	private JobDetailFactoryBean createJobDetail(Class<MyTeamCronSchedularJob> jobClass) {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(jobClass);
		factoryBean.setDurability(true);
		return factoryBean;
	}

	private CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, long cronExpression) {
		CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
		factoryBean.setJobDetail(jobDetail);
		factoryBean.setCronExpression(cronExp);
		factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
		return factoryBean;
	}
}