package moze_intel.projecte.emc.mappers.customConversions.json;

import java.util.Map;

/**
Java class to hold data from a json file with custom converrsions and values.

Format:

<pre>
{
	'comment': 'Optional comment describing the file',
	'groups': {
		'a name describing the group of conversions': {
			'comment':'An optional comment',
			'conversions': [
				//List of Conversions
			]
		},
		'another group': {...}
	},
	'values': {
		'before': {
			'a': 1, 'b': 2, 'c': 'free'
		},
		'after': {
			'd': 3
		}
	}
}
</pre>

The Conversions are encoded like this:
<pre>
{'output': 'outA',      'ingredients': {'ing1': 1, 'ing2': 2, 'ing3': 3}},
{   'out': 'outB',     'c': 3, 'ingr': {'ing1': 1, 'ing2': 1, 'ing3': 1}},
{     'o': 'outB', 'count': 3,    'i': ['ing1', 'ing2', 'ing3']}
{     'o': 'outC',                'i': ['ing1', 'ing1', 'ing1']}
</pre>
The default output-count is 1.

The second and third example conversions are identical. <br/>

Valid names for items include:
<ul>
<li><tt>'modId:itemname|metadatavalue'</tt></li>
<li><tt>'OD|oreDictionaryName'</tt></li>
<li><tt>'FAKE|identifier for a fake item across this file'</tt></li>
</ul>
*/
public class CustomConversionFile
{
	public String comment;
	public Map<String, ConversionGroup> groups;
	public FixedValues values;
}
