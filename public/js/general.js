/*
	General JS for all sites
*/
/*********************************************
Responsive setting
**********************************************/
+function(){
	var navdrawerContainer = document.querySelector('.navdrawer-container');
      var appbarElement = document.querySelector('.app-bar');
      var darkbgElement = document.querySelector('.navdrawer-bg');
      var menuBtn = document.querySelector('.menu');
      menuBtn.addEventListener('click', function() {
        var isOpen = navdrawerContainer.classList.contains('open');
        if(isOpen) {
          appbarElement.classList.remove('open');
          navdrawerContainer.classList.remove('open');
          darkbgElement.classList.remove('open');
        } else {
          appbarElement.classList.add('open');
          navdrawerContainer.classList.add('open');
          darkbgElement.classList.add('open');
        }
      }, true);
	  var navdrawerBg = document.querySelector('.navdrawer-bg');
	  navdrawerBg.addEventListener('click', function(){
		var isOpen = navdrawerBg.classList.contains('open');
		if(isOpen){
		  appbarElement.classList.remove('open');
          navdrawerContainer.classList.remove('open');
          darkbgElement.classList.remove('open');
		}
	  })
}();
/*********************************************
Stores client dimension info (client width & height)
**********************************************
+function($){
	var getWindowSize = function(){
		var myWidth = 0, myHeight = 0;
		  if( typeof( window.innerWidth ) == 'number' ) {
			//Non-IE
			myWidth = window.innerWidth;
			myHeight = window.innerHeight;
		  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
			//IE 6+ in 'standards compliant mode'
			myWidth = document.documentElement.clientWidth;
			myHeight = document.documentElement.clientHeight;
		  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
			//IE 4 compatible
			myWidth = document.body.clientWidth;
			myHeight = document.body.clientHeight;
		  }
		return([myWidth, myHeight]);
	}
		
	var store_dim = function(){
		var size = getWindowSize();
		if(typeof(sessionStorage) != "undefined"){
			sessionStorage.window_width = size[0];
			sessionStorage.window_height = size[1];
		}else if(typeof(Storage) != "undefined"){
			Storage.window_width = size[0];
			Storage.window_height = size[1];
		}else{
			window.w_dim_qifenbao = size;
		}
	}
	
	$(window).resize(store_dim);
	$(document).ready(store_dim);
 }(jQuery);

 
 
 
/*********************************************
defined default actions of elements
**********************************************
+function($){
	var preview_panel = $("#article-preview");
	
	// get client dimension
	var getWindowSize = function(){
		if(typeof(localStorage) != "undefined"){
			return([
			parseInt(localStorage.getItem("window_width")),
			parseInt(localStorage.getItem("window_height"))]);
		}else if(typeof(Storage) != "undefined"){
			return([
				parseInt(Storage.window_width),
				parseInt(Storage.window_height)]);
		}else{
			return(window.w_dim_qifenbao);
		}
	}
	
	// brand height resize
	//$(".home-brand").height(getWindowSize()[1]*3/4);		// 75vh
	
	// then we can get right parameters
	var brandHeight = $(".home-brand").height()-50;
	var scrollPos_prev = brandHeight;
	var logo_original_height = $(".navbar-logo").height();

	// defined something matters with scroll to top
	var scroll_down = function(){
		$("body,html").animate(
			{"scrollTop":brandHeight}, 200, function(){
				scrollPos_prev = brandHeight+0.5;
				$(".navbar-logo").animate({
					opacity:"1"
				})
			}
		);
		$(".navbar-logo").animate({
			opacity:"0.5"
		}, 150)
	}
	
	// defined something matters with scroll bar
	var scroll_listener = function(){
		var scrollPos = $(window).scrollTop();
		var logo = $(".navbar-logo");
		var nav_height = $("nav").height();
		var logoPos = logo.offset().top;
		brandHeight = $(".home-brand").height()-50;
		
		if(scrollPos_prev < 1 &&
				scrollPos_prev < scrollPos && 
				scrollPos < brandHeight){
			scroll_down();
		}
		
		var logo_size = brandHeight + nav_height - scrollPos
		if(getWindowSize()[0] < 992){
			
		} else if(logo.height() != logo_original_height && 
			logo_size > logo_original_height){
			logo.css({
			  "margin-top": (-logo_original_height+nav_height)+"px",
			  "height": logo_original_height,
			  "width": logo_original_height
			})
		}else if(scrollPos <= brandHeight && 
			logo_size < logo_original_height){
			logo.css({
			  "margin-top": (-logo_size+nav_height)+"px",
			  "height": logo_size,
			  "width": logo_size
			})
		}else if(logo_size < 50){
			logo.css({
			  "margin-top": (-50+nav_height)+"px",
			  "height": "50px",
			  "width": "50px"
			})
		}
		$("#test").text(scrollPos_prev+", "+ scrollPos);
		
		
		// preview
		if(scrollPos > brandHeight+50){
			preview_panel.css({
				"position": "fixed",
			});
		} else {
			preview_panel.css({
				"position": "absolute",
			});
		}
		
		
		if(scrollPos >=0){
			scrollPos_prev = scrollPos+0.5;
		}
		else{
			scrollPos_prev = 0.5;
		}
	}
	
	
	// defines the action when mouse enter search field
	var onMouseEnterBrand = function(){
			var brand = $(".home-brand-jumbotron");
			var h = brandHeight-40;
			var hotkeywords = $(".home-brand-jumbotron .home-brand-hotkeys")
			brand.off("mouseenter");
			brand.animate({
				"height": (brandHeight - 40)+"px",
			},200);
			$(".home-brand-jumbotron .hidden").removeClass("hidden");
			h = h-105-$("brand-form").height();
			if(h > 40){
				
				var label_h = $(".brand-label:first").height()+23;
				var h_proper = Math.ceil((h - 40)/label_h) * label_h - 14;
				hotkeywords.css({
					"max-height": h_proper+"px",
					"height": h_proper+"px",
				})
			} else{
				hotkeywords.remove();
			}
		}
		
	var brand_label_onFocus = function(){
		$(this).fadeTo(10,1);
	}
	var brand_label_loseFocus = function(){
		$(this).fadeTo(10,0.6);
	}
	
	// reg functions 
	$(document).ready(function(){
		$(window).scroll(scroll_listener);
		$(window).scrollTop(brandHeight);
		$(".home-brand-jumbotron").mouseenter(onMouseEnterBrand)
		$(".brand-label").hover(brand_label_onFocus, brand_label_loseFocus)
		$(".rocket").click(scroll_down);
		
		$(".article-index").hover(function(){
			var preview = $(this).find(".preview").html();
			preview_panel.width($(".col-md-3").width());
			if(preview != undefined){
				preview_panel.children(".content").html(preview);
				preview_panel.find("img").width("100%");
				preview_panel.stop(true, true);
				preview_panel.fadeIn();
			}
		}, function(){
			preview_panel.stop(true, false)
			preview_panel.fadeOut();
		});
	});
}(jQuery);

*/