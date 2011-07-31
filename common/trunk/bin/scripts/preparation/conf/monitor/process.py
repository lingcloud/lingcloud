#!/usr/bin/python
#
# Copyright (C) 2008-2011,
# LingCloud Team,
# Institute of Computing Technology,
# Chinese Academy of Sciences.
# P.O.Box 2704, 100190, Beijing, China.
#
# http://lingcloud.org
#

import commands

descriptors = list()
ps_stat = 0
ps_output = ''
proc_list = list()

def collect_proc():
	global ps_stat
	global ps_output
	global proc_list

	cmd = 'ps -eo pid,user,s,cmd'

	(ps_stat, ps_output) = commands.getstatusoutput(cmd)
	proc_list = ps_output.splitlines()

def get_total_procs(name):
	global proc_list

	collect_proc()
	return len(proc_list)

def get_zombie_procs(name):
	global ps_stat
	global proc_list

	collect_proc()
	count = 0;
	for i in proc_list:
		p = i.split()
	if p[2].find('Z') >= 0:
		count+=1

	return count

def get_httpd(name):
	global proc_list

	collect_proc()
	ret = 0
	for i in proc_list:
		if i.find('/httpd') > 0:
			ret = 1
			break

	return ret

def get_mysql(name):
	global proc_list

	collect_proc()
	ret = 0
	for i in proc_list:
		if i.find('/mysqld') > 0:
			ret = 1
			break

	return ret

def get_xend(name):
	global proc_list

	collect_proc()
	ret = 0
	for i in proc_list:
		if i.find('xend') > 0:
			ret = 1
			break

	return ret

def get_lingcloud(name):
	global proc_list

	collect_proc()
	ret = 0
	for i in proc_list:
		if i.find('lingcloud.home') > 0:
			ret = 1
			break

	return ret

def metric_init(params):
	global descriptors

	d1 = {'name': 'total_process',
		'call_back': get_total_procs,
		'time_max': 90,
		'value_type': 'uint',
		'units': 'count',
		'slope': 'both',
		'format': '%u',
		'description': 'Get total process number.',
		'groups': 'process'}

	d2 = {'name': 'zombie_process',
		'call_back': get_zombie_procs,
		'time_max': 90,
		'value_type': 'uint',
		'units': 'count',
		'slope': 'zero',
		'format': '%hu',
		'description': 'Get zombie process number.',
        	'groups': 'process'}

	d3 = {'name': 'httpd',
		'call_back': get_httpd,
		'time_max': 90,
		'value_type': 'uint',
		'units': 'flag',
		'slope': 'zero',
		'format': '%hu',
		'description': 'Get httpd status.',
        	'groups': 'process'}

	d4 = {'name': 'mysql',
		'call_back': get_mysql,
		'time_max': 90,
		'value_type': 'uint',
		'units': 'flag',
		'slope': 'zero',
		'format': '%hu',
		'description': 'Get Mysql status.',
        	'groups': 'process'}

	d5 = {'name': 'xend',
		'call_back': get_xend,
		'time_max': 90,
		'value_type': 'uint',
		'units': 'flag',
		'slope': 'zero',
		'format': '%hu',
		'description': 'Get xend status.',
        	'groups': 'process'}

	d6 = {'name': 'lingcloud',
		'call_back': get_lingcloud,
		'time_max': 90,
		'value_type': 'uint',
		'units': 'flag',
		'slope': 'zero',
		'format': '%hu',
		'description': 'Get lingcloud status.',
        	'groups': 'process'}

	descriptors = [d1,d2,d3,d4,d5,d6]
	return descriptors

def metric_cleanup():
	'''Clean up the metric module.'''
	pass

#This code is for debugging and unit testing	
if __name__ == '__main__':
	params = {'RandomMax': '500',
		'ConstantValue': '322'}
	metric_init(params)
	for d in descriptors:
		v = d['call_back'](d['name'])
		print 'value for %s is %u' % (d['name'],  v)

