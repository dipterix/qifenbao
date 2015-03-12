+function($){
	var image_adjust = function(){
		var images = $("article img");
		images.each(function(){
			if($(this).width() > 500) {
				$(this).css({
					"width": "100%",
				});
			}
		});
	}
	$(document).ready(function(){
		image_adjust();
	})
}(jQuery);


/*+function($){
		// get client dimension
		var getWindowSize = function(){
			if(typeof(sessionStorage) != "undefined"){
				return([
				parseInt(sessionStorage.getItem("window_width")),
				parseInt(sessionStorage.getItem("window_height"))]);
			}else if(typeof(Storage) != "undefined"){
				return([
					parseInt(Storage.window_width),
					parseInt(Storage.window_height)]);
			}else{
				return(window.w_dim_qifenbao);
			}
		}
		var embed = $(".article-content embed");
		var scrollPos = 0;
		var ads = $("#aside");
		var ads_bottom = $(".footer").height() || 0;
		var adsHeight = parseInt(ads.height());
		var brand_height = parseInt($(".home-brand").height());
		var scroll_listener = function(){
			scrollPos = parseInt($(window).scrollTop());
			if(ads.length > 0){
				var adsPos = ads.parent().offset().top;
				var height = parseInt(getWindowSize()[1]);
				
				$(".rocket").html(				(adsPos+adsHeight) +"<br>"+ (scrollPos+height))
				if(adsPos+adsHeight < scrollPos+height && brand_height < scrollPos){
					if(ads.css("position") != "fixed"){
						ads.css({
							"position":"fixed",
						});
						
						ads.animate({
							"bottom" : (ads_bottom + 50)+"px",
						});
					}
				}else{
					ads.css({
						"position":"relative",
						"bottom" : "0px",
					});
				}
			}
		}
		
		var re_index = function(){
			var rank = $(".comment-content .comment-rank");
			var ind = 0;
			rank.each(function(){$(this).text(++ind)})
		}
		
		var paginator = function(){
			var ind = parseInt($("#pagination-panel").attr("data-info"));
			var page = ind -1;
			var pag_num = $("#pagination-panel .pagination-num");
			
			pag_num.each(function(){
				if(page > 0){
					if(page == ind){
						$(this).attr("data-link", "#");
					}else{
						$(this).attr("data-link", "/page/"+(page));
					}
					$(this).children().text(page);
				} else{
					$(this).addClass("hidden");
					$(".pagination-dots").addClass("hidden");
				}
				page += 1;
			});
			
			pag_num.click(function(){
				window.location = $(this).attr("data-link");
			})
		}
		
		$(document).ready(function(){
			embed.height(embed.width()*2/3);
			ads.width(ads.parent().width());
			$(window).scroll(scroll_listener);
			re_index();
		})
	}(jQuery);*/