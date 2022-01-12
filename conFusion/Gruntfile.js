'use strict'

module.exports = function(grunt) {
    // Time of how long tasks take, can help optimize build times
    require('time-grunt')(grunt);

    // Automatically load required Grunt tasks
    require('jit-grunt')(grunt, {
        useminPrepare: 'grunt-usemin'
    });

    // Define the configuration for all the tasks
    grunt.initConfig({
        sass:{
            dist:{
                files:{
                    'css/styles.css':'css/styles.scss'
                }
            }
        },

        copy:{
            html:{
                files:[{
                    // for html
                    expand: true,
                    dot: true,
                    cwd: './',
                    src:['*.html'],
                    dest:'dist'            
                }]
            },

            fonts: {
                files: [
                {
                    //for font-awesome
                    expand: true,
                    dot: true,
                    cwd: 'node_modules/font-awesome',
                    src: ['fonts/*.*'],
                    dest: 'dist'
                }]
            }
        },

        clean:{
            build:{
                src: ['dist/']
            }
        },

        imagemin:{
            dynamic:{
                files:[{
                    expand: true,
                    cwd:'./',
                    src:['img/*.{png,jpg,gif}'],
                    dest:'dist/'
                }]
            }
        },

        useminPrepare:{
            foo:{
                dest:'dist',
                src:['contactus.html','aboutus.html','index.html']
            },
            options:{
                flow:{
                    steps:{
                        css:['cssmin'],
                        js:['uglify']
                    },
                    post:{
                        css:[{
                            name:'cssmin',
                            createConfig: function(context, block){
                                var generated = context.options.generated;
                                generated.options = {
                                    keepSpecialComments: 0, rebase:false
                                };
                            }
                        }]
                    }
                }
            }
        },

        concat:{
            options:{
                separator: ';'
            },

            dist:{}
        },

        uglify:{
            dist:{}
        },

        cssmin:{
            dist:{}
        },

        filerev:{
            options:{
                encoding:'utf8',
                algorithm:'md5',
                length:20
            },

            release:{
                files:[{
                    src:[
                        'dist/js/*.js',
                        'dist/css/*.css'
                    ]
                }]
            }
        },

        usemin:{
            html:['dist/contactus.html','dist/aboutus.html','dist/index.html'], //*
            options:{
                assestDirs: ['dist', 'dist/css', 'dist/js']
            }
        },

        htmlmin:{
            dist:{
                options:{
                    collapseWhitespace:true
                },
                files:{
                    'dist/index.html': 'dist/index.html',  // 'destination': 'source'
                    'dist/contactus.html': 'dist/contactus.html',
                    'dist/aboutus.html': 'dist/aboutus.html'
                }
            }
        },

        watch:{
            files: 'css/*.scss',
            tasks: ['sass']
        },

        browserSync:{
            dev:{
                bsFiles:{
                    src:[
                        'css/*.css',
                        '*.html',
                        'js/*.js'
                    ]
                },
                options:{
                    watchTask: true,
                    server:{
                        baseDir: "./"
                    }
                }
            }
        }
    });

    grunt.registerTask('css',['sass']);
    grunt.registerTask('default',['browserSync'],['watch']);
    grunt.registerTask('build', [
        'clean',
        'copy',
        'imagemin',
        'useminPrepare',
        'concat',
        'cssmin',
        'uglify',
        'filerev',
        'usemin',
        'htmlmin'
    ]);
};