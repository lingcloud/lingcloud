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

def get_current_user(name):
	cmd = 'who'
	(stat, out) = commands.getstatusoutput(cmd)

	return out.count('\n') + 1

def metric_init(params):
	global descriptors

	d1 = {'name': 'current_user',
		'call_back': get_current_user,
		'time_max': 90,
		'value_type': 'uint',
		'units': 'N',
		'slope': 'both',
		'format': '%u',
		'description': 'Count the current users.',
		'groups': 'user'}

	descriptors = [d1]
	return descriptors

def metric_cleanup():
	'''Clean up the metric module.'''
	pass

#This code is for debugging and unit testing    
if __name__ == '__main__':
	params = {'RandomMax': '500',
		'test': '322'}
	metric_init(params)
	for d in descriptors:
		v = d['call_back'](d['name'])
		print 'value for %s is %u' % (d['name'],  v)

