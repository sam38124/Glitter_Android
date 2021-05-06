

class TopBar extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            searchValue:''
        };
    }
    render(){
        return <div class={"topDV"}>
            <div className={"topBar"}>
                <h3>𝒫𝑒𝓉𝒶𝑔𝓇𝒶𝓂</h3>
                <div className={"serchDiv"}>
                    <input placeholder={"請輸入查詢內容"} value={this.state.searchValue}
                           onChange={event => {
                               this.setState({searchValue: event.target.value})
                           }}></input>
                    <img className={"searchBt"} src={'../img/zoom2.png'}></img>
                </div>
                <div style={{flex:"auto"}}></div>
                <svg className="topIcon" style={{fill: "white"}}>
                    <path
                        d="M18.81,8.94L7.75,20L4,16.25L15.06,5.19L18.81,8.94z M3,21h3.75L3,17.25V21z M20.71,7.04c0.39-0.39,0.39-1.02,0-1.41  l-2.34-2.34c-0.39-0.39-1.021-0.39-1.41,0l-0.83,0.83l3.75,3.75L20.71,7.04z"></path>
                </svg>
               <i className="uil mdi-bell topIcon"></i>
            </div>
        </div>
    }
}
// var Home=createReactClass({
//     getInitialState:function (){
//         return {}
//     },
//     render:function (){
//         return <div>Hello</div>
//     }
// })

ReactDOM.render(<TopBar />,
    document.getElementById('infinite-example-two'));
