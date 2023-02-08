# Request Attribute Contributor Web

A quick POC to access request attributes from a Liferay Template (aka Fragment) for the following use cases

## Detection of DNT header

Liferay Analytics Cloud documents that it won't track users that send the "Do not track" header, aka DNT. 

If you're preparing an AC demo and your browsers are sending this header, it's crucial that you detect this early, before you're sending days worth of traffic. 

## Access all request attributes

In some cases it's useful to inspect the request attributes - e.g. in order to see what information you _could_ access if you needed to. 

## How to use

* Clone within a Liferay Workspace's module directory as `request-attribute-contributor-web`
* Compile
* Deploy
* Create a fragment, including styling according to your needs. (See below for a samples)
* Add fragment to any page

### DNT Sample Fragment

#### HTML

(best used in a Master Page)

	<div class="fragment_dnt">
		[#if dnt]
		<div class="dnt-alert">
			<span>
				<a href="https://learn.liferay.com/analytics-cloud/latest/en/workspace-data/data-control-and-privacy.html#do-not-track-feature" 
				target="_blank">DNT</a> detected!<br/>
			    No AC demo! (<a href="https://allaboutdnt.com/" target="_blank">Why?</a>)
			</span>
		</div>
		[/#if]
		[#if layoutMode == 'edit']
		<p>
		  This fragment will display a ribbon on the page, when the DNT header 
		  is detected. It signals that Analytics Cloud will not track - and in 
		  demos you might want to deactivate that behavior in your browser.
		</p>
		<p>
			_This_ text only appears when the page is in edit mode, so that 
			you can locate the fragment. 
		</p>
		[/#if]
	</div>

#### CSS

	.fragment_dnt .dnt-alert {
		  position: fixed;
		  width: 300px;
		  height: 300px;
		  overflow: hidden;
		  position: absolute;
		  top: -10px;
		  right: -10px;
		  z-index:1000;
	    transition: opacity 1s ease-in-out;
		  display:block;
	}
	
	.hidden .fragment_dnt .dnt-alert {
		visibility:hidden; 
		opacity:0;
	  transition: opacity 1s ease-in-out, visibility 1s;
	}
	
	.fragment_dnt .dnt-alert::before,
	.fragment_dnt .dnt-alert::after {
		  position: absolute;
		  z-index: -1;
		  content: '';
		  display: block;
		  border-top-color: red;
		  border-right-color: red;
	}
		
	.fragment_dnt .dnt-alert::before {
		  top: -10;
		  left: -10;
	}
		
	.fragment_dnt .dnt-alert::after {
		  bottom: 0;
		  right: 0;
	}
		
	.fragment_dnt .dnt-alert span {
		  position: absolute;
		  display: block;
		  width: 505px;
		  padding: 35px 0;
		  background-color: var(--warning);
		  opacity:.7;
		  box-shadow: 0 5px 10px rgba(0,0,0,.1);
		  color: #fff;
		  font: 700 18px/1 sans-serif;
		  text-shadow: 0 1px 1px rgba(0,0,0,.2);
		  text-transform: uppercase;
		  text-align: center;
		  left: -70px;
		  top: 70px;
		  transform: rotate(45deg);
	}


#### JS

	fragmentElement.onmouseover=function(){
	  fragmentElement.classList.add("hidden");
		setTimeout(function(){
	        fragmentElement.classList.remove("hidden");
	  	}, 5000);
	}

#### Sample Screenshot

![sample-dnt.png](sample-dnt.png)

### Request Attribute Dumper Sample Fragment:

    <div class="fragment_requestAttributes">
	    ${requestAttributes}
    </div>

### Fragment sample CSS:

	.fragment_requestAttributes .attributeName {
		font-weight:bold;
	}
	.fragment_requestAttributes .attributeVisualization {
		border: 1px solid grey;
	}
	
### Sample Screenshot:

![sample-request-attributes.png](sample-request-attributes.png)
