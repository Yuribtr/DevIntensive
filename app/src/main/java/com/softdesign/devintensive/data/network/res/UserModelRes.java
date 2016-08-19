package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserModelRes {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private Data data;

    /**
     *
     * @return
     * The success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     *
     * @return
     * The data
     */
    public Data getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("user")
        @Expose
        private User user;
        @SerializedName("token")
        @Expose
        private String token;

        /**
         *
         * @return
         * The user
         */
        public User getUser() {
            return user;
        }

        /**
         *
         * @param user
         * The user
         */
        public void setUser(User user) {
            this.user = user;
        }

        /**
         *
         * @return
         * The token
         */
        public String getToken() {
            return token;
        }

        /**
         *
         * @param token
         * The token
         */
        public void setToken(String token) {
            this.token = token;
        }

    }

    public class User {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("second_name")
        @Expose
        private String secondName;
        @SerializedName("__v")
        @Expose
        private int v;
        @SerializedName("repositories")
        @Expose
        private Repositories repositories;
        @SerializedName("contacts")
        @Expose
        private Contacts contacts;
        @SerializedName("profileValues")
        @Expose
        private ProfileValues profileValues;
        @SerializedName("publicInfo")
        @Expose
        private PublicInfo publicInfo;
        @SerializedName("specialization")
        @Expose
        private String specialization;
        @SerializedName("role")
        @Expose
        private String role;
        @SerializedName("updated")
        @Expose
        private String updated;

        /**
         *
         * @return
         * The id
         */
        public String getId() {
            return id;
        }

        /**
         *
         * @param id
         * The _id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The firstName
         */
        public String getFirstName() {
            return firstName;
        }

        /**
         *
         * @param firstName
         * The first_name
         */
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        /**
         *
         * @return
         * The secondName
         */
        public String getSecondName() {
            return secondName;
        }

        /**
         *
         * @param secondName
         * The second_name
         */
        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        /**
         *
         * @return
         * The v
         */
        public int getV() {
            return v;
        }

        /**
         *
         * @param v
         * The __v
         */
        public void setV(int v) {
            this.v = v;
        }

        /**
         *
         * @return
         * The repositories
         */
        public Repositories getRepositories() {
            return repositories;
        }

        /**
         *
         * @param repositories
         * The repositories
         */
        public void setRepositories(Repositories repositories) {
            this.repositories = repositories;
        }

        /**
         *
         * @return
         * The contacts
         */
        public Contacts getContacts() {
            return contacts;
        }

        /**
         *
         * @param contacts
         * The contacts
         */
        public void setContacts(Contacts contacts) {
            this.contacts = contacts;
        }

        /**
         *
         * @return
         * The profileValues
         */
        public ProfileValues getProfileValues() {
            return profileValues;
        }

        /**
         *
         * @param profileValues
         * The profileValues
         */
        public void setProfileValues(ProfileValues profileValues) {
            this.profileValues = profileValues;
        }

        /**
         *
         * @return
         * The publicInfo
         */
        public PublicInfo getPublicInfo() {
            return publicInfo;
        }

        /**
         *
         * @param publicInfo
         * The publicInfo
         */
        public void setPublicInfo(PublicInfo publicInfo) {
            this.publicInfo = publicInfo;
        }

        /**
         *
         * @return
         * The specialization
         */
        public String getSpecialization() {
            return specialization;
        }

        /**
         *
         * @param specialization
         * The specialization
         */
        public void setSpecialization(String specialization) {
            this.specialization = specialization;
        }

        /**
         *
         * @return
         * The role
         */
        public String getRole() {
            return role;
        }

        /**
         *
         * @param role
         * The role
         */
        public void setRole(String role) {
            this.role = role;
        }

        /**
         *
         * @return
         * The updated
         */
        public String getUpdated() {
            return updated;
        }

        /**
         *
         * @param updated
         * The updated
         */
        public void setUpdated(String updated) {
            this.updated = updated;
        }

        public class Contacts {

            @SerializedName("vk")
            @Expose
            private String vk;
            @SerializedName("phone")
            @Expose
            private String phone;
            @SerializedName("email")
            @Expose
            private String email;
            @SerializedName("updated")
            @Expose
            private String updated;

            /**
             *
             * @return
             * The vk
             */
            public String getVk() {
                return vk;
            }

            /**
             *
             * @param vk
             * The vk
             */
            public void setVk(String vk) {
                this.vk = vk;
            }

            /**
             *
             * @return
             * The phone
             */
            public String getPhone() {
                return phone;
            }

            /**
             *
             * @param phone
             * The phone
             */
            public void setPhone(String phone) {
                this.phone = phone;
            }

            /**
             *
             * @return
             * The email
             */
            public String getEmail() {
                return email;
            }

            /**
             *
             * @param email
             * The email
             */
            public void setEmail(String email) {
                this.email = email;
            }

            /**
             *
             * @return
             * The updated
             */
            public String getUpdated() {
                return updated;
            }

            /**
             *
             * @param updated
             * The updated
             */
            public void setUpdated(String updated) {
                this.updated = updated;
            }

        }

        public class ProfileValues {

            @SerializedName("homeTask")
            @Expose
            private int homeTask;
            @SerializedName("projects")
            @Expose
            private int projects;
            @SerializedName("linesCode")
            @Expose
            private int linesCode;
            @SerializedName("likesBy")
            @Expose
            private List<String> likesBy = new ArrayList<String>();
            @SerializedName("rait")
            @Expose
            private int rait;
            @SerializedName("updated")
            @Expose
            private String updated;
            @SerializedName("rating")
            @Expose
            private int rating;

            /**
             *
             * @return
             * The homeTask
             */
            public int getHomeTask() {
                return homeTask;
            }

            /**
             *
             * @param homeTask
             * The homeTask
             */
            public void setHomeTask(int homeTask) {
                this.homeTask = homeTask;
            }

            /**
             *
             * @return
             * The projects
             */
            public int getProjects() {
                return projects;
            }

            /**
             *
             * @param projects
             * The projects
             */
            public void setProjects(int projects) {
                this.projects = projects;
            }

            /**
             *
             * @return
             * The linesCode
             */
            public int getLinesCode() {
                return linesCode;
            }

            /**
             *
             * @param linesCode
             * The linesCode
             */
            public void setLinesCode(int linesCode) {
                this.linesCode = linesCode;
            }

            /**
             *
             * @return
             * The likesBy
             */
            public List<String> getLikesBy() {
                return likesBy;
            }

            /**
             *
             * @param likesBy
             * The likesBy
             */
            public void setLikesBy(List<String> likesBy) {
                this.likesBy = likesBy;
            }

            /**
             *
             * @return
             * The rait
             */
            public int getRait() {
                return rait;
            }

            /**
             *
             * @param rait
             * The rait
             */
            public void setRait(int rait) {
                this.rait = rait;
            }

            /**
             *
             * @return
             * The updated
             */
            public String getUpdated() {
                return updated;
            }

            /**
             *
             * @param updated
             * The updated
             */
            public void setUpdated(String updated) {
                this.updated = updated;
            }

            /**
             *
             * @return
             * The rating
             */
            public int getRating() {
                return rating;
            }

            /**
             *
             * @param rating
             * The rating
             */
            public void setRating(int rating) {
                this.rating = rating;
            }

        }

        public class PublicInfo {

            @SerializedName("bio")
            @Expose
            private String bio;
            @SerializedName("avatar")
            @Expose
            private String avatar;
            @SerializedName("photo")
            @Expose
            private String photo;
            @SerializedName("updated")
            @Expose
            private String updated;

            /**
             *
             * @return
             * The bio
             */
            public String getBio() {
                return bio;
            }

            /**
             *
             * @param bio
             * The bio
             */
            public void setBio(String bio) {
                this.bio = bio;
            }

            /**
             *
             * @return
             * The avatar
             */
            public String getAvatar() {
                return avatar;
            }

            /**
             *
             * @param avatar
             * The avatar
             */
            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            /**
             *
             * @return
             * The photo
             */
            public String getPhoto() {
                return photo;
            }

            /**
             *
             * @param photo
             * The photo
             */
            public void setPhoto(String photo) {
                this.photo = photo;
            }

            /**
             *
             * @return
             * The updated
             */
            public String getUpdated() {
                return updated;
            }

            /**
             *
             * @param updated
             * The updated
             */
            public void setUpdated(String updated) {
                this.updated = updated;
            }

        }

        public class Repositories {

            @SerializedName("repo")
            @Expose
            private List<Repo> repo = new ArrayList<Repo>();
            @SerializedName("updated")
            @Expose
            private String updated;

            /**
             *
             * @return
             * The repo
             */
            public List<Repo> getRepo() {
                return repo;
            }

            /**
             *
             * @param repo
             * The repo
             */
            public void setRepo(List<Repo> repo) {
                this.repo = repo;
            }

            /**
             *
             * @return
             * The updated
             */
            public String getUpdated() {
                return updated;
            }

            /**
             *
             * @param updated
             * The updated
             */
            public void setUpdated(String updated) {
                this.updated = updated;
            }

            public class Repo {

                @SerializedName("_id")
                @Expose
                private String id;
                @SerializedName("git")
                @Expose
                private String git;
                @SerializedName("title")
                @Expose
                private String title;

                /**
                 *
                 * @return
                 * The id
                 */
                public String getId() {
                    return id;
                }

                /**
                 *
                 * @param id
                 * The _id
                 */
                public void setId(String id) {
                    this.id = id;
                }

                /**
                 *
                 * @return
                 * The git
                 */
                public String getGit() {
                    return git;
                }

                /**
                 *
                 * @param git
                 * The git
                 */
                public void setGit(String git) {
                    this.git = git;
                }

                /**
                 *
                 * @return
                 * The title
                 */
                public String getTitle() {
                    return title;
                }

                /**
                 *
                 * @param title
                 * The title
                 */
                public void setTitle(String title) {
                    this.title = title;
                }

            }

        }


    }

}
