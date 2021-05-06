var ListItem = createReactClass({
    getDefaultProps: function() {
        return {
            height: 50,
            lineHeight: "50px"
        }
    },
    render: function() {
        if(this.props.data===undefined){
            return  ``
        }
        var that=this
        var data2=this.props.data2
        var html2=``
        if(data2!==undefined){
            html2=<div id="item" style={{width:'50%'}} onClick={function (e){e.preventDefault();glitter.openNewTab(`https://asms.coa.gov.tw/Amlapp/app/AnnounceList.aspx?Id=${that.props.data2.AnimalId}&AcceptNum=${that.props.data2.AcceptNum}&PageType=Adopt`);}}>
                <div className="item">
                    <div className="itemImg"><img id="image6" src={`https://asms.coa.gov.tw/Amlapp/Upload/pic/${this.props.data2.pic}`}></img>
                    </div>
                    <div className="titleList">
                        <div className="itemTitle"><p>．</p>品種:{this.props.data2.BreedName}</div>
                        <div className="itemTitle"><p>．</p>性別:{this.props.data2.SexName}</div>
                        <div className="itemTitle"><p>．</p>地點:<marquee>{this.props.data2.ShelterName}</marquee></div>
                    </div>
                </div>
            </div>
        }
        return <div className="infinite-list-item" style={
            {
                height: this.props.height,
                lineHeight: this.props.lineHeight,
                display:"flex"
            }
        }>
            <div id="item" style={{width:'50%'}} onClick={ function (e){e.preventDefault();glitter.openNewTab(`https://asms.coa.gov.tw/Amlapp/app/AnnounceList.aspx?Id=${that.props.data.AnimalId}&AcceptNum=${that.props.data.AcceptNum}&PageType=Adopt`);}}>
            <div className="item">
                <div className="itemImg"><img id="image6" src={`https://asms.coa.gov.tw/Amlapp/Upload/pic/${this.props.data.pic}`}></img>
                </div>
                <div className="titleList">
                    <div className="itemTitle"><p>．</p>品種:{this.props.data.BreedName}</div>
                    <div className="itemTitle"><p>．</p>性別:{this.props.data.SexName}</div>
                    <div className="itemTitle"><p>．</p>地點:<marquee>{this.props.data.ShelterName}</marquee></div>
                </div>
            </div>
        </div>
            {html2}
        </div>;
    }
});
var VariableInfiniteList = createReactClass({
    getInitialState: function() {
        return {
            elementHeights: [],
            isInfiniteLoading: false,
            data:[]
        };
    },
    generateVariableElementHeights: function(number,height) {
        var heights = [];
        for (var i = 0; i < number; i++) {
            heights.push(height);
        }
        return heights;
    },
    handleInfiniteLoad: function() {
        if(this.state.isInfiniteLoading){return}
        var that = this;
        this.setState({
            isInfiniteLoading: true
        });
        // $('#loadingView').remove()
        // $('#itemList').append(glitter.publicBeans.getLoadingView)
        $.ajax({
            url: `https://asms.coa.gov.tw/Asms/api/ViewNowAnimal?pageSize=200&currentPage=${this.currentPage+1}&sortDirection=DESC&sortFields=AcceptDate`,
            timeout: 60000,
            contentType: "application/text; charset=utf-8;",
            type: "get",
            dataType: "json",
            success: function (data) {
                that.currentPage=that.currentPage+1
                data=data.filter(function (dd){return dd.pic!==''})
                that.setState({
                    isInfiniteLoading: false,
                    elementHeights: that.state.elementHeights.concat(that.generateVariableElementHeights(data.length/2,$('body').width()/2+60)),
                    data:that.state.data.concat(data)
                });
            },
            error: function (data) {
                that.handleInfiniteLoad()
            }
        });

    },
    elementInfiniteLoad: function() {
        return <div className="infinite-list-item">
            Loading...
        </div>;
    },
    render: function() {
        var that=this
        var elements = this.state.elementHeights.map(function(el, i) {
            return <ListItem key={i} index={i} height={el} lineHeight={el + "px"} data={that.state.data[i*2]} data2={that.state.data[i*2+1]}/>;
            })
        return <Infinite
            elementHeight={this.state.elementHeights}
                         containerHeight={$('#infinite-example-two').height()}
                         infiniteLoadBeginEdgeOffset={200}
                         onInfiniteLoad={this.handleInfiniteLoad}
                         loadingSpinnerDelegate={this.elementInfiniteLoad()}
                         isInfiniteLoading={this.state.isInfiniteLoading}
                         timeScrollStateLastsForAfterUserScrolls={1000}
        >
            {elements}
        </Infinite>;
    },
    //現在的API請求頁面
    currentPage:0
});


ReactDOM.render(<VariableInfiniteList/>,
    document.getElementById('infinite-example-two'));


